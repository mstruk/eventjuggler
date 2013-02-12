package org.eventjuggler.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.eventjuggler.model.Address;
import org.eventjuggler.model.Group;
import org.eventjuggler.model.User;

/**
 * Backing bean for Group entities.
 * <p>
 * This class provides CRUD functionality for all Group entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class GroupBean implements Serializable
{

    private static final long serialVersionUID = 1L;

    /*
     * Support creating and retrieving Group entities
     */

    private Long id;

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    private Group group;

    public Group getGroup()
    {
        return this.group;
    }

    @Inject
    private Conversation conversation;

    @PersistenceContext(unitName = "eventjuggler", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String create()
    {

        this.conversation.begin();
        return "create?faces-redirect=true";
    }

    public void retrieve()
    {

        if (FacesContext.getCurrentInstance().isPostback())
        {
            return;
        }

        if (this.conversation.isTransient())
        {
            this.conversation.begin();
        }

        if (this.id == null)
        {
            this.group = this.example;
        }
        else
        {
            this.group = findById(getId());
        }
    }

    public Group findById(Long id)
    {

        return this.entityManager.find(Group.class, id);
    }

    /*
     * Support updating and deleting Group entities
     */

    public String update()
    {
        this.conversation.end();

        try
        {
            if (this.id == null)
            {
                this.entityManager.persist(this.group);
                return "search?faces-redirect=true";
            }
            else
            {
                this.entityManager.merge(this.group);
                return "view?faces-redirect=true&id=" + this.group.getId();
            }
        }
        catch (Exception e)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete()
    {
        this.conversation.end();

        try
        {
            this.entityManager.remove(findById(getId()));
            this.entityManager.flush();
            return "search?faces-redirect=true";
        }
        catch (Exception e)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    /*
     * Support searching Group entities with pagination
     */

    private int page;
    private long count;
    private List<Group> pageItems;

    private Group example = new Group();

    public int getPage()
    {
        return this.page;
    }

    public void setPage(int page)
    {
        this.page = page;
    }

    public int getPageSize()
    {
        return 10;
    }

    public Group getExample()
    {
        return this.example;
    }

    public void setExample(Group example)
    {
        this.example = example;
    }

    public void search()
    {
        this.page = 0;
    }

    public void paginate()
    {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Group> root = countCriteria.from(Group.class);
        countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Group> criteria = builder.createQuery(Group.class);
        root = criteria.from(Group.class);
        TypedQuery<Group> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Group> root)
    {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        User owner = this.example.getOwner();
        if (owner != null)
        {
            predicatesList.add(builder.equal(root.get("owner"), owner));
        }
        String name = this.example.getName();
        if (name != null && !"".equals(name))
        {
            predicatesList.add(builder.like(root.<String> get("name"), '%' + name + '%'));
        }
        String description = this.example.getDescription();
        if (description != null && !"".equals(description))
        {
            predicatesList.add(builder.like(root.<String> get("description"), '%' + description + '%'));
        }
        String imageId = this.example.getImageId();
        if (imageId != null && !"".equals(imageId))
        {
            predicatesList.add(builder.like(root.<String> get("imageId"), '%' + imageId + '%'));
        }
        Address location = this.example.getLocation();
        if (location != null)
        {
            predicatesList.add(builder.equal(root.get("location"), location));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<Group> getPageItems()
    {
        return this.pageItems;
    }

    public long getCount()
    {
        return this.count;
    }

    /*
     * Support listing and POSTing back Group entities (e.g. from inside an
     * HtmlSelectOneMenu)
     */

    public List<Group> getAll()
    {

        CriteriaQuery<Group> criteria = this.entityManager.getCriteriaBuilder().createQuery(Group.class);
        return this.entityManager.createQuery(criteria.select(criteria.from(Group.class))).getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter()
    {

        final GroupBean ejbProxy = this.sessionContext.getBusinessObject(GroupBean.class);

        return new Converter()
        {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value)
            {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value)
            {

                if (value == null)
                {
                    return "";
                }

                return String.valueOf(((Group) value).getId());
            }
        };
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */

    private Group add = new Group();

    public Group getAdd()
    {
        return this.add;
    }

    public Group getAdded()
    {
        Group added = this.add;
        this.add = new Group();
        return added;
    }
}