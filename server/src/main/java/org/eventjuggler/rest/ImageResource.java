package org.eventjuggler.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.picketlink.extensions.core.pbox.authorization.UserLoggedIn;

@Path("/image")
public class ImageResource {

    private File imageDir;

    public ImageResource() {
        if (System.getProperties().containsKey("ej.image.dir")) {
            imageDir = new File(System.getProperty("ej.image.dir"));
        } else if (System.getProperties().containsKey("jboss.server.data.dir")) {
            imageDir = new File(System.getProperty("jboss.server.data.dir"), "ej-images");
        }

        if (!imageDir.isDirectory()) {
            imageDir.mkdirs();
        }
    }

    @GET
    @Path("/{id}")
    public Response getImage(@PathParam("id") String id) {
        File f = getImageFile(id);
        if (f.isFile()) {
            return Response.ok(f).header("Content-Disposition", "attachment; filename=" + f.getName()).type(getType(f)).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    private File getImageFile(String id) {
        return new File(imageDir, id);
    }

    private String getType(File image) {
        switch (image.getName().substring(image.getName().lastIndexOf('.') + 1)) {
            case "jpg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @UserLoggedIn
    public Response saveImage(@PathParam("id") String id, byte[] image) throws IOException {
        File f = getImageFile(id);
        FileOutputStream os = new FileOutputStream(f);
        os.write(image);
        os.close();
        return Response.ok().build();
    }

}
