import javax.persistence.*;
import test. Family;
import test.Person;

public class Main {
    private EntityManagerFactory factory;
    public void setUp() throws Exception {
        factory = Persistence.createEntityManagerFactory("people");
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();  //nowa transakcja
        Query q = em.createQuery("select m from Person m"); //lista rekordów
        boolean createNewEntries = (q.getResultList().size() == 0);
        if (createNewEntries) {// No, so lets create new entries
            Family family = new Family();
            family.setDescription("Family for the Knopfs");
            em.persist(family);
            for (int i = 0; i < 40; i++) {
                Person person = new Person();
                person.setFirstName("Jim_" + i);
                person.setLastName("Knopf_" + i);
                em.persist(person);
                family.getMembers().add(person);
                em.persist(person);
                em.persist(family);
            }
        }
        em.getTransaction().commit();
        em.close();
    }
    public void checkAvailablePeople()
    {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("select m from Person m");
// We should have 40 Persons in the database
        System.out.println(q.getResultList().size());
        em.close();
    }

    public void checkFamily()
    {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("select f from Family f");
// We should have one family with 40 persons
        System.out.println(q.getResultList().size());
        System.out.println(((Family)q.getSingleResult()).getMembers().size());
        em.close();
    }
    public void deletePerson()
    {
        EntityManager em = factory.createEntityManager();
        // Begin a new local transaction so that we can persist a new entity
        em.getTransaction().begin();
        Query q = em
                .createQuery("SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName");
        q.setParameter("firstName", "Jim_1");
        q.setParameter("lastName", "Knopf_!");
        Person user = (Person) q.getSingleResult();
        em.remove(user);
        em.getTransaction().commit();
        Person person = (Person) q.getSingleResult();
        // Begin a new local transaction so that we can persist a new entity
        em.close();
    }

    /*********** STUDENTS IMPLEMENT THEIR SHIT BELOW ************/
    public static void main(String[] args) throws Exception {
        Main fuckStaticClasses = new Main();
        fuckStaticClasses.setUp();
    }
}
