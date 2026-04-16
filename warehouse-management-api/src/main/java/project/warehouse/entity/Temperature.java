package project.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "temperatures")
public class Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    // Конструкторы
    public Temperature() {}

    public Temperature(String title) {
        this.title = title;
    }

    public Temperature(Integer id, String title) {
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