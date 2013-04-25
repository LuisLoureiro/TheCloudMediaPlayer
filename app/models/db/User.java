package models.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import play.db.jpa.JPA;

@Entity(name="users")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="type")
@DiscriminatorValue(value="other")
//@DiscriminatorOptions(force=true)
public class User {

	@Id
	private String id;
	private String email;
//	@NotNull
	@Column(nullable=false)
	private String type;
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="first_auth_id")
	private User firstAuth;
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="firstAuth")
	private List<User> relatedAuth;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getFirstAuth() {
		return firstAuth;
	}

	public void setFirstAuth(User firstAuth) {
		this.firstAuth = firstAuth;
	}

	public List<User> getRelatedAuth() {
		return relatedAuth;
	}

	public void setRelatedAuth(List<User> relatedAuth) {
		this.relatedAuth = relatedAuth;
	}
	
	// DATA ACCESS METHODS
    /**
     * Find a User by id.
     */
    public static User findById(String id) {
        return JPA.em().find(User.class, id);
    }
    
    /**
     * Create a custom query.
     */
    public static TypedQuery<User> createQuery(String query) {
    	return JPA.em().createQuery(query, User.class);
    }
    
    /**
     * Update this User.
     */
    public void update() {
        JPA.em().merge(this);
    }
    
    /**
     * Insert this new user.
     */
    public void save() {
        JPA.em().persist(this);
    }
    
    /**
     * Delete this user.
     */
    public void delete() {
        JPA.em().remove(this);
    }
}
