package com.asaitec.fruitstore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FruitStore {

	public static void main(String[] args) {
		SpringApplication.run(FruitStore.class, args);
	}
	
	@Bean
	CommandLineRunner runner(){
		return args -> {
			//Reading pricing file
			Map<String, Double> pricing = new HashMap<String, Double>();
			Path path = Paths.get(getClass().getClassLoader().getResource("pricing.txt").toURI());
		    Stream<String> lines = Files.lines(path);
		    lines.skip(1).forEach(line -> {
		    	String[] price = line.split(",");
		    	pricing.put(price[0].trim(), new Double(price[1].trim()));
		    });
		    lines.close();
		    
		    //Reading shopping file
		    Map<String, Integer> shopping = new HashMap<String, Integer>();
			path = Paths.get(getClass().getClassLoader().getResource("shopping.txt").toURI());
		    lines = Files.lines(path);
		    lines.skip(1).forEach(line -> {
		    	String[] amount = line.split(",");
		    	shopping.put(amount[0].trim(), new Integer(amount[1].trim()));
		    });
		    lines.close();
		    
		    //Producing output
		    double total = 0.0;
		    for (Map.Entry<String, Integer> entry : shopping.entrySet()) {
		    	String product = entry.getKey();
		    	Integer amount = entry.getValue();
		        double partial = amount * pricing.get(product);
		        System.out.println(product + ", " + amount + " * " + pricing.get(product) + " = " + partial);
		        total += partial;
		    }
		    
		    //Applying offers
		    //Compre 3 y pague 2 en Manzanas
		    int apples = shopping.get("Manzana");
		    int freeApples = apples / 3;
		    double appleDiscount = freeApples * pricing.get("Manzana");
		    System.out.println("Compre 3 y pague 2 en Manzanas: -" + appleDiscount);
		    total -= appleDiscount;
		    
		    //Llévese 2 Peras y la primera Naranja le sale gratis
		    int pears = shopping.get("Pera");
		    if (pears >= 2) {
		    	double orangeDiscount = pricing.get("Naranja");
		    	System.out.println("Llévese 2 Peras y la primera Naranja le sale gratis: -" + orangeDiscount);
		    	total -= orangeDiscount;
		    }
		    
		    //Por cada 4 € gastados en Peras, le descontamos un euro su factura final
		    int pearDiscount = (int) (pears * pricing.get("Pera")) / 4;
		    if (pearDiscount > 0) {
			    System.out.println("Por cada 4 € gastados en Peras, le descontamos un euro su factura final: -" + pearDiscount);
			    total -= pearDiscount;
		    }
		    
		    System.out.println("Total, " + total);
		};
	}
}
