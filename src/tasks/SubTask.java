package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private Epic epic;
    private int epicId;

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, Epic epic, Status status) {
        super(name, description, duration, startTime);
        this.epic = epic;
        this.epicId = epic != null ? epic.getId() : -1;
        this.setStatus(status);
    }

    // Геттер и сеттер для epicId
    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    // Геттер и сеттер для эпика
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
                ", продолжительность=" + getDuration() +
                ", время начала=" + getStartTime() +
                ", время завершения=" + getEndTime() +
                '}';
    }

    @Override
    public String toCsvString() {
        return String.format("SubTask,%d,%s,%s,%s,%d,%s,%d",
                getId(), getName(), getDescription(), getStatus(),
                getDuration().toMinutes(), getStartTime(), getEpicId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return getId() == subTask.getId() &&
                Objects.equals(getName(), subTask.getName()) &&
                Objects.equals(getDescription(), subTask.getDescription()) &&
                Objects.equals(getEpic(), subTask.getEpic()) &&
                Objects.equals(getDuration(), subTask.getDuration()) &&
                Objects.equals(getStartTime(), subTask.getStartTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getEpic(), getDuration(), getStartTime());
    }
}
