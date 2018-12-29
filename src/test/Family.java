package test;
import java.util.*;
import javax.persistence.*;
@Entity
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String description;

    @OneToMany(mappedBy = "family")
    private final List<Person> members = new ArrayList<Person>();

    public int getId() {  return id;  }
    public void setId(int id) {  this.id = id;  }
    public String getDescription() { return description; }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public List<Person> getMembers() {  return members;  }

}
