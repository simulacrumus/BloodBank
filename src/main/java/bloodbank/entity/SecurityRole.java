/**
 * File: SecurityRole.java
 * Course materials (21W) CST 8277
 *
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * update by : I. Am. A. Student 040nnnnnnn
 *
 */
package bloodbank.entity;

import static bloodbank.entity.SecurityRole.ROLE_BY_NAME_QUERY;

import java.io.Serializable;
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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Role class used for (JSR-375) Java EE Security authorization/authentication
 */
@Entity
@Access( AccessType.FIELD)
@Table(name = "security_role")
@NamedQuery(name = ROLE_BY_NAME_QUERY, query = "select r from SecurityRole r where r.roleName = :param1")
public class SecurityRole implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public static final String ROLE_BY_NAME_QUERY = "roleByName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    protected int id;

    @JsonInclude(Include.NON_NULL)
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    protected String roleName;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "roles")
    protected Set<SecurityUser> users;

    public SecurityRole() {
        super();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonIgnore
    public Set<SecurityUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SecurityUser> users) {
        this.users = users;
    }

    public void addUserToRole(SecurityUser user) {
        getUsers().add(user);
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
        SecurityRole other = (SecurityRole)obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }
}