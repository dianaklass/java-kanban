package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.subTasks = new ArrayList<>();
        this.endTime = calculateEndTime();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = subTasks;
        updateEpicData();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) return;
        subTasks.add(subTask);
        updateEpicData();
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        updateEpicData();
    }

    private LocalDateTime calculateEndTime() {
        LocalDateTime latestEndTime = getStartTime();

        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime() != null) {
                LocalDateTime subTaskEndTime = subTask.getStartTime().plus(subTask.getDuration());
                if (subTaskEndTime.isAfter(latestEndTime)) {
                    latestEndTime = subTaskEndTime;
                }
            }
        }
        return latestEndTime;
    }

    private void updateStatus() {
        if (subTasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean hasInProgress = false;
        boolean hasNew = false;
        boolean allNew = true;

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                hasInProgress = true;
            }
            if (subTask.getStatus() == Status.NEW) {
                hasNew = true;
            } else {
                allNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            setStatus(Status.NEW);
        } else if (allDone) {
            setStatus(Status.DONE);
        } else if (hasInProgress || hasNew) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.NEW);
        }
    }


    public void updateEpicData() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = LocalDateTime.MAX;

        for (SubTask subTask : subTasks) {
            totalDuration = totalDuration.plus(subTask.getDuration());
            if (subTask.getStartTime() != null && subTask.getStartTime().isBefore(earliestStartTime)) {
                earliestStartTime = subTask.getStartTime();
            }
        }

        this.setStartTime(earliestStartTime);
        this.endTime = calculateEndTime();

        updateStatus();
    }


    @Override
    public String toString() {
        return String.format("Epic{id=%d, name='%s', description='%s', status=%s, duration=%d min, startTime=%s, endTime=%s}",
                getId(), getName(), getDescription(), getStatus(), getDuration().toMinutes(), getStartTime(), getEndTime());
    }

    @Override
    public String toCsvString() {
        return String.format("Epic,%d,%s,%s,%s",
                getId(), getName(), getDescription(), getStatus());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return getId() == epic.getId() &&
                Objects.equals(getName(), epic.getName()) &&
                Objects.equals(getDescription(), epic.getDescription()) &&
                getStatus() == epic.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getStatus());
    }
}
