package com.calendar.data;

import java.util.List;

/**
 * Created by NAVER on 2017-08-03.
 */
public class ICalFilteredData {
    private List<ICalTodo> todoList;
    private List<ICalFilteredEvent> eventList;

    public List<ICalTodo> getTodoList() {
        return todoList;
    }

    public void setTodoList(List<ICalTodo> todoList) {
        this.todoList = todoList;
    }

    public List<ICalFilteredEvent> getEventList() {
        return eventList;
    }

    public void setEventList(List<ICalFilteredEvent> eventList) {
        this.eventList = eventList;
    }
}
