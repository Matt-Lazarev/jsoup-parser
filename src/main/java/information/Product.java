package information;

import java.util.Arrays;

public class Product {
    private String title;

    private String description;

    private String type;

    private String originalNumber;

    private String deliveryTime;

    private String[] compatibility;

    private String cost;

    private String comment;


    public Product(String title, String description, String type, String originalNumber, String deliveryTime, String[] compatibility, String cost, String comment) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.originalNumber = originalNumber;
        this.deliveryTime = deliveryTime;
        this.compatibility = compatibility;
        this.cost = cost;
        this.comment = comment;
    }

    @Override
    public String toString(){
        return  "Наименование товара: " + title + "\n" +
                "Описание товара: " + description + "\n" +
                "Тип: " + type + "\n" +
                "Оригинальный номер: " + originalNumber + "\n" +
                "Срок доставки: " + deliveryTime + "\n" +
                "Применимость: " + "\n" + Arrays.toString(compatibility) + "\n" +
                "Стоимость: " + cost + "\n" +
                "Комментарий: " + comment;
    }
}

