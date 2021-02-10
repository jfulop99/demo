package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RestController
public class TomcatController {


    @RequestMapping("/")
    public String index() {
        return "Say hello!";
    }


    @GetMapping("/hello")
    public String sayHello() {
        Optional<Result> result = calculateWithStream(List.of(100, 120, 40, 70, 200, 30, 50));

        return "<h1>" +  result.toString() + "</h1>";

        //        return IntStream.range(0, 10)
//                .mapToObj(i -> "Hello number " + i)
//                .collect(Collectors.toList());
    }

    @GetMapping("/city")
    public String printCities() throws ParseException {
        Cities cities = new Cities();

        try {
            cities.readFile(Path.of(cities.getClass().getResource("/iranyitoszamok-varosok-2021.csv").toURI()));
        } catch (URISyntaxException uriSyntaxException) {
            uriSyntaxException.printStackTrace();
        }

        return cities.printCitiesByAlphabetOrder();


    }



    public Optional<Result> calculateWithStream(List<Integer> rates) {

        int sizeOfInput = rates.size();

        return IntStream.range(0, sizeOfInput - 1)
                .boxed()
                .flatMap(a -> IntStream.range(a + 1, sizeOfInput)
                        .mapToObj(b -> new Result(a, b, rates.get(b) - rates.get(a))))
                .max(Comparator.comparingInt(Result::getDiff));


    }

}

