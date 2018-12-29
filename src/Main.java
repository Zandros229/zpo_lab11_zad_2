import javax.persistence.*;
import test. Family;
import test.Job;
import test.Person;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private EntityManagerFactory factory;

    public void setUp() throws Exception {
        factory = Persistence.createEntityManagerFactory("people");

        setUpJobs();

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
                person.setJobList(getRandomJobs());
                em.persist(person);
                family.getMembers().add(person);
                em.persist(person);
                em.persist(family);
            }
        }
        em.getTransaction().commit();
        em.close();
    }

    public List<Job> getAllJobs() throws Exception {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("select m from Job m");
        List<Job> result = q.getResultList();
        em.close();
        return result;
    }

    public List<Job> getRandomJobs() throws Exception {
        List<Job> j0bs = getAllJobs();
        Random random = new Random();
        return j0bs.stream().filter(j -> random.nextBoolean()).collect(Collectors.toList());
    }

    public List<Job> getJobListForGivenPerson(Person person) {
        return getJobListForGivenPerson(person, false);
    }

    public List<Job> getJobListForGivenPerson(Person person, boolean print) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("SELECT m.jobList FROM Person m WHERE m.id = " + person.getId());
        List<Job> jobs = q.getResultList();
        if(jobs.size() == 0) return Collections.emptyList();
        if(print) {
            System.out.println("--- " + person.getFirstName() + " " + person.getLastName() + " ---");
            if (jobs != null && jobs.size() > 0) jobs.forEach(j -> {
                if (j != null) System.out.println(j.getJobDescr() + " " + j.getSalery());
            });
            else System.out.println("! BEZROBOTNY !");
        }
        em.close();
        return jobs;
    }

    public void printTotalSalaryFromAllJobs(Person person) {
        List<Job> jobs = getJobListForGivenPerson(person);
        System.out.println("Suma zarobków: " + jobs.stream().mapToDouble(Job::getSalery).sum());
    }

    public double getTotalSalaryFromAllJobs(Person person) {
        List<Job> jobs = getJobListForGivenPerson(person);
        if(jobs.size() == 0) return 0.0;
        double sum = 0.0;
        for(Job job: jobs) {
            if(job != null)
                sum += job.getSalery();
        }
        return sum;
    }

    public void printFamilySalary() {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("SELECT f FROM Family f");
        Family family = (Family) q.getSingleResult();
        em.close();

        double avg = 0.0;
        int members = 0;

        for(Person member: family.getMembers()) {
            avg += getTotalSalaryFromAllJobs(member);
            members++;
        }

        avg = avg / ((double) members);

        System.out.println("Srednie zarobki rodziny: " + avg);
    }

    public void setUpJobs() throws Exception {
        EntityManager em = factory.createEntityManager();
        em.getTransaction().begin();  //nowa transakcja
        Query q = em.createQuery("select m from Job m"); //lista rekordów
        boolean createNewEntries = (q.getResultList().size() == 0);
        if (createNewEntries) {// No, so lets create new entries
            Job newJob = new Job();
            newJob.setJobDescr("Murarz");
            newJob.setSalery(69);
            em.persist(newJob);

            newJob = new Job();
            newJob.setJobDescr("Tynkarz");
            newJob.setSalery(99.7);
            em.persist(newJob);

            newJob = new Job();
            newJob.setJobDescr("Akrobata");
            newJob.setSalery(66.6);
            em.persist(newJob);
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

    public Person getPersonFromDB(String name, String surname) {
        EntityManager em = factory.createEntityManager();
        Query q = em.createQuery("SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName");
        q.setParameter("firstName", name);
        q.setParameter("lastName", surname);

        try {
            Person person = (Person) q.getSingleResult();
            return person;
        } catch (Exception e) {
            System.err.println("Can't get person from database!");
            return null;
        } finally {
            em.close();
        }
    }

    /*********** STUDENTS IMPLEMENT THEIR SHIT BELOW ************/
    public static void main(String[] args) throws Exception {
        Main fuckStaticClasses = new Main();
        fuckStaticClasses.setUp();
        fuckStaticClasses.checkAvailablePeople();
        fuckStaticClasses.checkFamily();

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("1) lista prac i zarobków dla wybranego członka rodziny\n" +
                    "2) suma zarobków dla członka rodziny\n" +
                    "3) średnie zarobki dla całej rodziny\n");

            int choice = -1;
            String name, surname;
            try {
                choice = scanner.nextInt();

            } catch (Exception e) {
                return;
            }
            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Name: ");
                    name = scanner.nextLine();

                    System.out.print("Surname: ");
                    surname = scanner.nextLine();

                    Person person = fuckStaticClasses.getPersonFromDB(name, surname);
                    if(person != null) fuckStaticClasses.getJobListForGivenPerson(person, true);
                    break;
                case 2:
                    scanner.nextLine();
                    System.out.print("Name: ");
                    name = scanner.nextLine();

                    System.out.print("Surname: ");
                    surname = scanner.nextLine();

                    person = fuckStaticClasses.getPersonFromDB(name, surname);
                    if(person != null) fuckStaticClasses.printTotalSalaryFromAllJobs(person);
                    break;
                case 3:
                    fuckStaticClasses.printFamilySalary();
                    break;
                default:
                    return;
            }
        }
    }
}
