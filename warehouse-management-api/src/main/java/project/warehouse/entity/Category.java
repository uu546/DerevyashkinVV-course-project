package project.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String title;

    // Конструкторы
    public Category() {}

    public Category(String title) {
        this.title = title;
    }

    public Category(Integer id, String title) {
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
