package tasks;

import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "id=" + getId() +
                ", имя='" + getName() + '\'' +
                ", описание='" + getDescription() + '\'' +
                ", статус=" + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return getId() == subTask.getId() &&
                Objects.equals(getName(), subTask.getName()) &&
                Objects.equals(getDescription(), subTask.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription());
    }
}
