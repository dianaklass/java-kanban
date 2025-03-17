package ru.yandex.practicum.main.service;

import ru.yandex.practicum.main.model.Epic;

import ru.yandex.practicum.main.model.Status;

import ru.yandex.practicum.main.model.SubTask;

import ru.yandex.practicum.main.model.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private static int identificator = 1;

    private Map<Integer, Task> tasks = new HashMap<>();


    private final HistoryManager historyManager;


    public InMemoryTaskManager() {


        this.historyManager = Manager.getDefaultHistory();


    }


    @Override


    public void addTask(Task task) {


        for (Task availability : tasks.values()) {


            if (availability.equals(task)) {


                task.setId(availability.getId());


                return;


            }


        }


        task.setId(identificator++);


        tasks.put(task.getId(), task);


        if (task instanceof SubTask) {


            SubTask subTask = (SubTask) task;


            Epic epic = subTask.getEpic();


            if (epic != null) {


                epic.addSubTask(subTask);


                updateEpicStatus(epic);


            }


        }


    }


    @Override


    public List<Task> getAllTasks() {


        List<Task> taskList = new ArrayList<>();


        for (Task task : tasks.values()) {


            if (task instanceof Task) {


                taskList.add(task);


            }


        }


        return taskList;


    }


    @Override


    public List<Epic> getAllEpics() {


        List<Epic> epicList = new ArrayList<>();


        for (Task task : tasks.values()) {


            if (task instanceof Epic) {


                epicList.add((Epic) task);


            }


        }


        return epicList;


    }


    @Override


    public List<SubTask> getAllSubTasks() {


        List<SubTask> subTaskList = new ArrayList<>();


        for (Task task : tasks.values()) {


            if (task instanceof SubTask) {


                subTaskList.add((SubTask) task);


            }


        }


        return subTaskList;


    }


    @Override


    public void clearAllTasks() {


        List<Task> tasksToRemove = new ArrayList<>();


        for (Task task : tasks.values()) {


            if (task instanceof Task) {


                tasksToRemove.add(task);


            }


        }


        for (Task task : tasksToRemove) {


            tasks.remove(task.getId());


        }


    }


    @Override


    public void clearAllEpics() {


        List<Epic> epicsToRemove = new ArrayList<>();


        for (Task task : tasks.values()) {


            if (task instanceof Epic) {


                Epic epic = (Epic) task;


                epic.getSubTask().clear();


            }


        }


        for (Epic epic : epicsToRemove) {


            tasks.remove(epic.getId());


        }


    }


    @Override


    public void clearAllSubTasks() {


        for (Task task : tasks.values()) {


            if (task instanceof Epic) {


                Epic epic = (Epic) task;


                epic.getSubTask().clear();


                epic.setStatus(Status.NEW);


            }


        }


    }


    @Override


    public Task getTaskById(int id) {


        Task task = tasks.get(id);


        if (task != null) {


            historyManager.add(task);


        }


        return task;


    }


    @Override


    public void clearById(int id) {


        Task task = tasks.get(id);


        if (task == null) {


            printError("Задача с айди " + id + " не найдена.");


            return;


        }


        if (task instanceof Epic) {


            Epic epic = (Epic) task;


            for (SubTask subTask : epic.getSubTask()) {


                tasks.remove(subTask.getId());


            }


            epic.getSubTask().clear();


            tasks.remove(id);


        } else if (task instanceof SubTask) {


            SubTask subTask = (SubTask) task;


            Epic epic = subTask.getEpic();


            if (epic != null) {


                epic.getSubTask().remove(subTask);


                updateEpicStatus(epic);


            }


            tasks.remove(id);


        } else {


            tasks.remove(id);


        }


    }


    @Override


    public void update(Task newTask) {


        if (newTask == null) {


            printError("Задача пуста");


            return;


        }


        int taskId = newTask.getId();


        if (newTask instanceof SubTask) {


            SubTask newSubTask = (SubTask) newTask;


            SubTask existingSubTask = (SubTask) tasks.get(taskId);


            Epic epic = existingSubTask.getEpic();


            if (epic != null && tasks.containsKey(taskId)) {


                epic.getSubTask().remove(existingSubTask);


                epic.getSubTask().add(newSubTask);


                newSubTask.setEpic(epic);


                updateEpicStatus(epic);


            } else {


                printError("Задачи с таким айди не была найдена");


            }


            tasks.put(taskId, newSubTask);


            System.out.println("Подзадача была обновлена");


        } else {


            if (tasks.containsKey(taskId)) {


                tasks.put(taskId, newTask);


                System.out.println("Задача была обновлена");


            } else {


                printError("Задачи с таким айди не была найдена");


            }


        }


    }


    @Override


    public List<SubTask> getSubTaskList(int epicId) {


        Task task = tasks.get(epicId);


        if (task != null && task.getClass() == Epic.class) {


            Epic epic = (Epic) task;


            List<SubTask> subTasks = epic.getSubTask();


            if (!subTasks.isEmpty()) {


                return new ArrayList<>(subTasks);


            } else {


                System.out.println("Эпик " + epic.getName() + " не содержит подзадач.");


                return new ArrayList<>();


            }


        } else {


            printError("Задача не является эпиком.");


            return new ArrayList<>();


        }


    }


    @Override


    public List<SubTask> updateTaskStatus(Task task, Status newStatus) {


        task.setStatus(newStatus);


        if (task instanceof Epic) {


            Epic epic = (Epic) task;


            List<SubTask> subTasks = epic.getSubTask();


            boolean hasNew = false;


            boolean hasDone = false;


            for (SubTask subTask : subTasks) {


                if (subTask.getStatus() == Status.NEW) {


                    hasNew = true;


                }


                if (subTask.getStatus() == Status.DONE) {


                    hasDone = true;


                }


            }


            if (subTasks.isEmpty() || !hasNew) {


                task.setStatus(Status.NEW);


            } else if (hasDone && !hasNew) {


                task.setStatus(Status.DONE);


            } else {


                task.setStatus(Status.IN_PROGRESS);


            }


            return subTasks;


        }


        return null;


    }


    @Override


    public void updateEpicStatus(Epic epic) {


        List<SubTask> subTasks = epic.getSubTask();


        boolean recent = true;


        boolean done = true;


        for (SubTask subTask : subTasks) {


            if (subTask.getStatus() != Status.NEW) {


                recent = false;


            }


            if (subTask.getStatus() != Status.DONE) {


                done = false;


            }


        }


        if (subTasks.isEmpty() || recent) {


            epic.setStatus(Status.NEW);


        } else if (done) {


            epic.setStatus(Status.DONE);


        } else {


            epic.setStatus(Status.IN_PROGRESS);


        }


    }


    @Override


    public List<Task> getHistory() {


        return historyManager.getHistory();


    }


}
