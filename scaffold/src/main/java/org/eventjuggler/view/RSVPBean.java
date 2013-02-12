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

import org.eventjuggler.model.RSVP;
import org.eventjuggler.model.User;

/**
 * Backing bean for RSVP entities.
 * <p>
 * This class provides CRUD functionality for all RSVP entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class RSVPBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving RSVP entities
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

   private RSVP RSVP;

   public RSVP getRSVP()
   {
      return this.RSVP;
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
         this.RSVP = this.example;
      }
      else
      {
         this.RSVP = findById(getId());
      }
   }

   public RSVP findById(Long id)
   {

      return this.entityManager.find(RSVP.class, id);
   }

   /*
    * Support updating and deleting RSVP entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.RSVP);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.RSVP);
            return "view?faces-redirect=true&id=" + this.RSVP.getId();
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
    * Support searching RSVP entities with pagination
    */

   private int page;
   private long count;
   private List<RSVP> pageItems;

   private RSVP example = new RSVP();

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

   public RSVP getExample()
   {
      return this.example;
   }

   public void setExample(RSVP example)
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
      Root<RSVP> root = countCriteria.from(RSVP.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<RSVP> criteria = builder.createQuery(RSVP.class);
      root = criteria.from(RSVP.class);
      TypedQuery<RSVP> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<RSVP> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      User user = this.example.getUser();
      if (user != null)
      {
         predicatesList.add(builder.equal(root.get("user"), user));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<RSVP> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back RSVP entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<RSVP> getAll()
   {

      CriteriaQuery<RSVP> criteria = this.entityManager.getCriteriaBuilder().createQuery(RSVP.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(RSVP.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final RSVPBean ejbProxy = this.sessionContext.getBusinessObject(RSVPBean.class);

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

            return String.valueOf(((RSVP) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private RSVP add = new RSVP();

   public RSVP getAdd()
   {
      return this.add;
   }

   public RSVP getAdded()
   {
      RSVP added = this.add;
      this.add = new RSVP();
      return added;
   }
}