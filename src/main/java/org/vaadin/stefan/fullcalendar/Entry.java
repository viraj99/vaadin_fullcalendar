package org.vaadin.stefan.fullcalendar;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents a event / item in the full calendar. It is named Entry here to prevent name conflicts with
 * event handling mechanisms (e.g. a component event fired by clicking something).
 * <p/>
 * <i><b>Note: </b>Creation of an entry might be exported to a builder later.</i>
 *
 */
public class Entry {
    private boolean editable;
    private final String id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean allDay;
    private String color;

    public Entry(String id, @Nonnull String title, @Nonnull LocalDateTime start, LocalDateTime end, boolean allDay, String color) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        this.id = id != null ? id : UUID.randomUUID().toString();
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.editable = true;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry event = (Entry) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    JsonObject toJson() {
        JsonObject jsonObject = Json.createObject();
        jsonObject.put("id", toJsonValue(getId()));
        jsonObject.put("title", toJsonValue(getTitle()));

        boolean fullDayEvent = isAllDay();
        jsonObject.put("allDay", toJsonValue(fullDayEvent));

        LocalDateTime start = getStart();
        LocalDateTime end = getEnd();
        jsonObject.put("start", toJsonValue(fullDayEvent ? start.toLocalDate() : start));
        jsonObject.put("end", toJsonValue(fullDayEvent ? end.toLocalDate() : end));
        jsonObject.put("editable", isEditable());
        jsonObject.put("color", toJsonValue(getColor()));

        return jsonObject;
    }

    /**
     * Updates this instance with the content of the given object. Properties, that are not part of the object will
     * be unmodified. Same for the id. Properties in the object, that do not match with this instance will be
     * ignored.
     * @param object json object / change set
     */
    void update(JsonObject object) {
        String id = object.getString("id");
        if (!this.id.equals(id)) {
            throw new IllegalArgumentException("IDs are not matching.");
        }

        updateString(object, "title", this::setTitle);
        updateBoolean(object, "editable", this::setEditable);
        updateBoolean(object, "allDay", this::setAllDay);
        updateDateTime(object, "start", this::setStart);
        updateDateTime(object, "end", this::setEnd);
        updateString(object, "color", this::setColor);
    }

    private void updateString(JsonObject object, String key, Consumer<String> setter) {
        if (object.hasKey(key)) {
            setter.accept(object.getString(key));
        }
    }

    private void updateBoolean(JsonObject object, String key, Consumer<Boolean> setter) {
        if (object.hasKey(key)) {
            setter.accept(object.getBoolean(key));
        }
    }


    private void updateDateTime(JsonObject object, String key, Consumer<LocalDateTime> setter) {
        if (object.hasKey(key)) {
            String string = object.getString(key);

            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(string);
            } catch (DateTimeParseException e) {
                dateTime = LocalDate.parse(string).atStartOfDay();
            }

            setter.accept(dateTime);
        }
    }

    private JsonValue toJsonValue(Object value) {
        if (value == null) {
            return Json.createNull();
        }
        if (value instanceof Boolean) {
            return Json.create((Boolean) value);
        }
        return Json.create(String.valueOf(value));
    }

    @Override
    public String toString() {
        return "Entry{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", allDay=" + allDay +
                ", editable=" + editable +
                ", id='" + id + '\'' +
                '}';
    }
}