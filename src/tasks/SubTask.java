package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;
    private Epic epic;

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, Epic epic, Status status) {
        super(name, description, duration, startTime);
        this.epicId = epic != null ? epic.getId() : -1;
        this.setStatus(status);
    }

    public Epic getEpic() {
        return epic;
    }


    public void setEpic(Epic epic) {
        this.epic = epic;
        this.epicId = epic != null ? epic.getId() : 0; // Обновляем ID эпика
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void setStatus(Status status) {
        super.setStatus(status);
        if (epic != null) {
            epic.updateEpicData();
        }
    }

    @Override
    public String toString() {
        return String.format("SubTask{id=%d, name='%s', description='%s', status=%s, duration=%d min, startTime=%s, endTime=%s, epicId=%d}",
                getId(), getName(), getDescription(), getStatus(), getDuration().toMinutes(), getStartTime(), getEndTime(), epicId);
    }

    @Override
    public String toCsvString() {
        return String.format("SubTask,%d,%s,%s,%s,%d,%s,%d",
                getId(), getName(), getDescription(), getStatus(),
                getDuration().toMinutes(), getStartTime(), epicId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return getId() == subTask.getId() &&
                Objects.equals(getName(), subTask.getName()) &&
                Objects.equals(getDescription(), subTask.getDescription()) &&
                Objects.equals(getDuration(), subTask.getDuration()) &&
                Objects.equals(getStartTime(), subTask.getStartTime()) &&
                epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getDuration(), getStartTime(), epicId);
    }
}

