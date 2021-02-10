package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cities {

    private static final String HUNGARIAN_COLLATOR_RULE = "<' ' < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS \n" +
            " < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J\n" +
            " < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó \n" +
            " < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T \n" +
            " < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS";

    private final RuleBasedCollator hunRuleBasedCollator = new RuleBasedCollator(HUNGARIAN_COLLATOR_RULE);

    private List<City> cityList;

    private List<City> cityListStream;

    Map<String, List<City>> cityMap;

    public Cities() throws ParseException {

        cityList = new ArrayList<>();

    }

    private static String getHungarianFirstLetter(City city) {
        String firstLetter = city.getName().substring(0, 1);
        if ("cCzZ".contains(firstLetter)) {
            if ("sS".contains(city.getName().substring(1, 2))) {
                return city.getName().substring(0, 2);
            }
        }
        if ("sS".contains(firstLetter)) {
            if ("zZ".contains(city.getName().substring(1, 2))) {
                return city.getName().substring(0, 2);
            }
        }
        if ("gGlLnNtT".contains(firstLetter)) {
            if ("yY".contains(city.getName().substring(1, 2))) {
                return city.getName().substring(0, 2);
            }
        }
        return firstLetter;
    }

    public void mapByFirstLetter() {
        cityMap = cityList
                .stream()
                .collect(Collectors.groupingBy(Cities::getHungarianFirstLetter));
    }

    public Map<Integer, List<City>> mapByLength() {

        Map<Integer, List<City>> result = cityList
                .stream()
                .collect(Collectors.groupingBy(city -> city.getName().length()));
        List<Integer> keys = new ArrayList<>(result.keySet());
        keys.sort(Comparator.naturalOrder());
        int i = 1;
        for (Integer key : keys) {
            int length = 76 + i++ * 2;
            System.out.println(String.format("%2d %3d %3$" + length + "s", key, result.get(key).size(), result.get(key).stream().sorted(Comparator.naturalOrder()).limit(4).collect(Collectors.toList())));
        }
        return result;
    }

    public void readFile(Path path) {
        try (Stream<String> lines = Files.lines(path, Charset.forName("UTF8"))) {
            cityList = lines
                    .skip(1)
                    .map(City::generateFromCsv)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read file", e);
        }
    }

    public String printCitiesByAlphabetOrder() {

        mapByFirstLetter();
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(cityMap.keySet());
        keys.sort(hunRuleBasedCollator);
        sb.append("""
                <!DOCTYPE html>
                <html>
                <body>
                                
                <h2>Városok ABC sorrendben</h2>
                                
                <table>
                  <tr>
                    <th> Kulcs </th>
                    <th> Db </th>
                    <th> Városok </th>
                  </tr>
                """);

        for (String key : keys) {
            sb.append("<tr>\n<td>" + key + "</td>\n<td>" + cityMap.get(key).size() + "</td>\n<td>" + cityMap.get(key)
                    .stream().sorted(Comparator.naturalOrder())
                    .collect(Collectors.toList()) + "</td>\n</tr>\n");
        }
        sb.append("""
                </table>
                                
                </body>
                </html>""");
        return sb.toString();
    }


    public long getNumberOfCities() {

        return cityList.stream()
                .map(City::getName)
                .distinct()
                .count();
    }

    public List<City> getCityListOrderedByName() {
        return cityList.stream()
                .sorted((Comparator.comparing(City::getName, hunRuleBasedCollator)
                        .thenComparing(City::getDistrictName, Comparator.nullsFirst(hunRuleBasedCollator))))
                .collect(Collectors.toList());
    }

    public Map<String, TreeSet<City>> citiesByAToZ() {

        Map<String, TreeSet<City>> result = new TreeMap<>(hunRuleBasedCollator);

        for (City city : cityList) {

            result.merge(city.getName().substring(0, 1), new TreeSet<>(List.of(city)), Cities::addNextCity);
            //result.computeIfAbsent(city.getName(), b -> new TreeSet<>()).add(city);


        }
        return result;
    }

    private static TreeSet<City> addNextCity(TreeSet<City> cities, TreeSet<City> cities2) {
        cities.addAll(cities2);
        return cities;
    }


    public static void main(String[] args) throws ParseException {

        Cities cities = new Cities();


        try {
            cities.readFile(Path.of(cities.getClass().getResource("/iranyitoszamok-varosok-2021.csv").toURI()));
        } catch (URISyntaxException uriSyntaxException) {
            uriSyntaxException.printStackTrace();
        }

        cities.printCitiesByAlphabetOrder();

        cities.mapByLength();

    }

}
