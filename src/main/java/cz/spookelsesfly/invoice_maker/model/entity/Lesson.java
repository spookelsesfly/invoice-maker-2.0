package cz.spookelsesfly.invoice_maker.model.entity;

import jakarta.persistence.*;

@Entity
@NamedQuery(
        name = "Lesson.findAll",
        query = "SELECT l FROM Lesson l"
)
@NamedQuery(
        name = "Lesson.findByType",
        query = "SELECT l FROM Lesson l WHERE l.type = :type"
)
@Table(name = "lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "lesson_id")
    private Integer lessonId;

    @Column(name = "type")
    private String type;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "price", nullable = false)
    private int price;

    public Lesson(){
    }

    public Integer getLessonId() {
        return lessonId;
    }

    public void setLessonId(Integer lessonId) {
        this.lessonId = lessonId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return type;
    }
}
