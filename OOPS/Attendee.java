public class Attendee {
    private String name;
    private String email;
    private int registrationId;

    public Attendee(String name, String email, int registrationId) {
        this.name = name;
        this.email = email;
        this.registrationId = registrationId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    @Override
    public String toString() {
        return "Registration ID: " + registrationId + ", Name: " + name + ", Email: " + email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendee attendee = (Attendee) o;
        return registrationId == attendee.registrationId;
    }

    @Override
    public int hashCode() {
        return registrationId;
    }
}
