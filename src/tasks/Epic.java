package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subTasks;

    // Поля для расчётных значений
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Конструктор
    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.subTasks = new ArrayList<>();
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime();
    }

    @Override
    public String toCsvString() {
        return String.format("Epic,%d,%s,%s,%s",
                getId(), getName(), getDescription(), getStatus());
    }

    // Метод для добавления подзадачи
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Подзадача пуста");
            return;
        }
        subTask.setEpic(this);
        subTasks.add(subTask);
        updateEpicData(); // Обновляем данные эпика после добавления подзадачи
    }

    // Метод для удаления подзадачи
    public void removeSubTask(SubTask subTask) {
        if (subTask != null) {
            subTasks.remove(subTask);
            updateEpicData(); // Обновляем данные эпика после удаления подзадачи
        }
    }

    // Метод для получения списка подзадач
    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Метод для вычисления времени завершения эпика
    private LocalDateTime calculateEndTime() {
        LocalDateTime latestEndTime = startTime;

        // Перебираем все подзадачи и находим самую позднюю
        for (SubTask subTask : subTasks) {
            LocalDateTime subTaskEndTime = subTask.getStartTime().plus(subTask.getDuration());
            if (subTaskEndTime.isAfter(latestEndTime)) {
                latestEndTime = subTaskEndTime;
            }
        }

        return latestEndTime;
    }

    // Метод для обновления данных эпика
    public void updateEpicData() {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = LocalDateTime.MAX;

        for (SubTask subTask : subTasks) {
            totalDuration = totalDuration.plus(subTask.getDuration());
            if (subTask.getStartTime().isBefore(earliestStartTime)) {
                earliestStartTime = subTask.getStartTime();
            }
        }

        this.duration = totalDuration;
        this.startTime = earliestStartTime;
        this.endTime = calculateEndTime();
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "id=" + getId() +
                ", имя='" + getName() + '\'' +
                ", описание='" + getDescription() + '\'' +
                ", статус=" + getStatus() +
                ", продолжительность=" + duration +
                ", время начала=" + startTime +
                ", время завершения=" + endTime +
                '}';
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
