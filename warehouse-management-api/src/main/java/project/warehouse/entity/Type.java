package project.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "types")
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    // Конструкторы
    public Type() {}

    public Type(String title) {
        this.title = title;
    }

    public Type(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}