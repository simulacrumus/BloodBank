/**
 * File: SecurityUser.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.entity;

import static bloodbank.entity.SecurityUser.SECURITY_USER_BY_NAME_QUERY;
import static bloodbank.entity.SecurityUser.USER_FOR_OWNING_PERSON_QUERY;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bloodbank.rest.serializer.SecurityRoleSerializer;

/**
 * User class used for (JSR-375) Java EE Security authorization/authentication
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "security_user")
@NamedQueries({
    @NamedQuery(name = USER_FOR_OWNING_PERSON_QUERY, query = "SELECT u FROM SecurityUser u WHERE u.person.id = :param1"),
    @NamedQuery(name = SECURITY_USER_BY_NAME_QUERY, query = "SELECT u FROM SecurityUser u WHERE u.username = :param1")})
public class SecurityUser implements Serializable, Principal {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public static final String USER_FOR_OWNING_PERSON_QUERY = "SecurityUser.userForOwningPerson";
    public static final String SECURITY_USER_BY_NAME_QUERY = "SecurityUser.userByName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected int id;
    
    @Basic(optional = false)
    protected String username;
    
    @Basic(optional = false)
    @Column(name="password_hash")
    protected String pwHash;
    
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    protected Person person;
    
    @ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "user_has_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    protected Set<SecurityRole> roles;

    public SecurityUser() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPwHash() {
        return pwHash;
    }

    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    @JsonInclude(Include.NON_NULL)
    @JsonSerialize(using = SecurityRoleSerializer.class)
    public Set<SecurityRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    // Principal
    @JsonIgnore
    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SecurityUser other = (SecurityUser)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}