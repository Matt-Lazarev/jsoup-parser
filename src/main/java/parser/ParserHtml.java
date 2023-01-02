package parser;

import information.Product;
import information.ProductHrefByTitle;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ParserHtml {

    private static final List<String> products = new ArrayList<>();

    private static final String baseUrl = "https://авторазбор.рф";

    private static final List<String> hrefs;

    private static Map<String, String> cookies;

    static {
        hrefs = List.of("https://авторазбор.рф/catalog/chevrolet/cruze-2009-2016",
                "https://авторазбор.рф/catalog/hyundai/solaris-accent-iv-2010",
                "https://авторазбор.рф/catalog/kia/rio-2011-2017",
                "https://авторазбор.рф/catalog/kia/rio-2017",
                "https://авторазбор.рф/catalog/volkswagen/polo-2009-2017",
                "https://авторазбор.рф/catalog/volkswagen/polo-2020");

        try {
            cookies = setConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        int count = 0;

        List<Product> allProductsDescriptions = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Products.txt"))) {

            for (int i = 0; i < 1; i++) {
                List<String> categoryHrefs = getAllCategories(hrefs.get(i));

                for (int j = 0; j < categoryHrefs.size(); j++) {
                    List<String> categoryItems = getAllCategoryItems(categoryHrefs.get(j));

                    for (int k = 0; k < categoryItems.size(); k++) {
                        ProductHrefByTitle products = getAllProducts(categoryItems.get(k));

                        for (int l = 0; l < products.size(); l++) {
                            Product p = getProductInformation(products.get(l), products.getTitle());
                            allProductsDescriptions.add(p);

                            writer.write(p.toString() + "\n\n");
                            count++;
                            System.out.println(count + ") " + p + "\n");
                        }
                    }
                }
            }
        }


    }

    public static List<String> getAllCategories(String href) throws IOException, InterruptedException {
        Thread.sleep(500 + new Random().nextInt(300));
        Document document = getDocument(href);

        Elements categories = document.select("li > .content-link.tree__link");

        List<String> categoryHref = new ArrayList<>();

        categories
                .stream()
                .map(x -> x.attr("href"))
                .forEach(x -> categoryHref.add(baseUrl + x));

        return categoryHref;
    }


    public static List<String> getAllCategoryItems(String href) throws IOException, InterruptedException {

        Thread.sleep(500 + new Random().nextInt(300));
        Document document = getDocument(href);

        Elements items = document.select("div.mobile-tree a.content-link");

        List<String> categoryItems = new ArrayList<>();

        items
                .stream()
                .map(x -> x.attr("href"))
                .forEach(x -> categoryItems.add(baseUrl + x));

        return categoryItems;
    }


    public static ProductHrefByTitle getAllProducts(String href) throws IOException, InterruptedException {
        Thread.sleep(500 + new Random().nextInt(300));

        Document document = getDocument(href);

        Elements products = document.select("div.catalog__title span.catalog__title-name > a.content-link");

        List<String> productHrefs = new ArrayList<>();

        products
                .stream()
                .map(x -> x.attr("href"))
                .forEach(x -> productHrefs.add(baseUrl + x));

        String title = products.size() > 0 ? products.get(0).text() : "";

        return new ProductHrefByTitle(productHrefs, title);
    }

    public static Product getProductInformation(String href, String productTitle) throws IOException, InterruptedException {

        Document document = getDocument(href);

        Elements compatibility = document.select("div.col-xs-12.col-md-8 a.catalog__compatibility");
        Elements information = document.select("div.col-xs-12.col-md-8");

        Elements description = document.select("div.row > h2");
        String desc = description.get(0).text();

        String title = productTitle.trim();
        String productDesc = desc.contains(title) ? desc.substring(desc.indexOf(title)).trim() : title;
        String type = "";
        String originalNum = "";
        String deliveryTime = "";
        String comment = "";
        String[] productCompatibility = makeArray(compatibility);
        String cost = "";

        for (Element el : information) {
            String text = el.text().toLowerCase();
            if (text.startsWith("тип")) {
                type = defaultSubstring(text);
            } else if (text.startsWith("оригинальный номер")) {
                originalNum = defaultSubstring(text);
            } else if (text.startsWith("срок доставки")) {
                deliveryTime = defaultSubstring(text);
            } else if (text.startsWith("комментарий")) {
                comment = defaultSubstring(text);
            } else if (text.startsWith("стоимость")) {
                String c = defaultSubstring(text);
                cost = c.substring(0, c.indexOf(" ")).trim();
            }
        }

        return new Product(title, productDesc, type, originalNum,
                deliveryTime, productCompatibility, cost, comment);

    }

    private static String[] makeArray(Elements compatibility) {
        List<String> cars = new ArrayList<>();
        for (Element el : compatibility) {
            String text = el.text().trim();
            cars.add(text);
        }

        return cars.toArray(new String[0]);
    }

    private static String defaultSubstring(String text) {
        return text.substring(text.indexOf(":") + 1).trim();
    }

    private static Map<String, String> setConnection () throws IOException {
        Connection.Response response = Jsoup.connect(baseUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .header("connection", "close")
                .header("Set-Cookie", "")
                .timeout(15_000)
                .followRedirects(true)
                .execute();

        return response.cookies();
    }

    private static Document getDocument(String url) throws IOException {

        return Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .cookies(cookies)
                .header("accept-language", "ru,en;q=0.9")
                .header("accept-encoding", "gzip, deflate, br")
                .header("cache-control", "max-age=0")
                .timeout(0)
                .get();
    }
}
