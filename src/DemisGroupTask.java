import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by admin on 02.12.2016.
 */
public class DemisGroupTask {

    /**
     * Метод поиска всех блоков в заданном тексте с введеными разделителями
     * @param text - исходный текст
     * @param leftBorder - правый разделитель
     * @param rightBorder - левый разделитель
     * @return - динамический массив из найденых блоков
     */
    ArrayList<String> findBlocks (String text, char leftBorder, char rightBorder) {
        // результат поиска
        ArrayList<String> blocks = new ArrayList<String>();
        // пробегаем по тексту
        for (int i=0; i<text.length();i++){
            // если нашли левый разделитель
            if (text.charAt(i)==leftBorder) {
                // запоминаем индекс следующей позиции
                int startPos=i+1;
                // создаем флаг для выхода из цикла поиска прового разделителя
                boolean isBorderFound=false;
                // поиск правого разделителя
                for (i=i+1;i<text.length()&& !isBorderFound;i++){
                    // если нашли правый разделитель
                    if (text.charAt(i)==rightBorder){
                        // меняем значение флага выхода из цикла
                        isBorderFound=true;
                        // если между разделителями есть хотя бы один символ
                        if (i-startPos>1) {
                            // добавляем найденную подстроку к результату
                            blocks.add(text.substring(startPos, i));
                        }
                    }
                }
                // сдвикаем итератор на позицию влево
                i=i-1;
            }
        }
        return blocks;
    }

    /**
     * Метод, предоставляющий отчет по найденным блокам текста в формате JSON
     * @param text - исходный текст
     * @param leftBorder - правый разделитель
     * @param rightBorder - левый разделитель
     * @return - отчет по найденным блокав в формате JSON
     */
    JSONObject getStat(String text, char leftBorder, char rightBorder){
        JSONObject stat=new JSONObject();
        // находим все подстроки для заданных разделителей
        ArrayList<String> blocks = findBlocks(text,leftBorder,rightBorder);
        // добавлям в отчет информацию о количестве подстрок
        stat.put("TotalBlocks",blocks.size());
        // находим среднюю длину блока текста в символах и добавлям ее в отчет
        double totalLength=0;
        for (int i=0;i<blocks.size();i++){
            totalLength = totalLength+blocks.get(i).length();
        }
        stat.put("AverageBlockLength",totalLength/blocks.size());
        // находим максимальную длину блока текста в символах и добавлям ее в отчет
        int maxLength=blocks.get(0).length();
        for (int i=1;i<blocks.size();i++){
            maxLength=Math.max(maxLength,blocks.get(i).length());
        }
        stat.put("MaxBlockLength",maxLength);
        // находим минимальную длину блока текста в символах и добавлям ее в отчет
        int minLength=blocks.get(0).length();
        for (int i=1;i<blocks.size();i++){
            minLength=Math.min(minLength,blocks.get(i).length());
        }
        stat.put("MinBlockLength",minLength);
        // создаем статистику для каждого блока и добавляем ее в отчет
        JSONArray blockStatAr=new JSONArray();
        for (int i=0; i<blocks.size(); i++){
            JSONObject curBlockStat=new JSONObject();
            // сам обнаруженный текст
            curBlockStat.put("BlockValue",blocks.get(i));
            // длина обнаруженного текста в символах
            curBlockStat.put("BlockLength",blocks.get(i).length());
            // количество символов латиницы (aA-zZ), кириллицы (аА-яЯ), арабских цифр (0-9) и других символов
            int russianSymbol=0;
            int latinSymbol=0;
            int digit=0;
            int other=0;
            for (int j=0;j<blocks.get(i).length();j++){
                if( isRussianSymbol(blocks.get(i).charAt(j))){
                    russianSymbol++;
                }else{
                    if( isLatinSymbol(blocks.get(i).charAt(j))){
                        latinSymbol++;
                    }else{
                        if( isDigit(blocks.get(i).charAt(j))){
                            digit++;
                        }else {
                            other++;
                        }
                    }
                }
            }
            curBlockStat.put("TotalLat",latinSymbol);
            curBlockStat.put("TotalCyr",russianSymbol);
            curBlockStat.put("TotalDigit",digit);
            curBlockStat.put("TotalOther",other);
            blockStatAr.put(curBlockStat);
        }
        stat.put("BlocksStat",blockStatAr);
        return stat;
    }

    /**
     * Проверка на принадлежность кирилице
     * @param c - символ в тексте
     * @return - истина, если символ кирилицы
     */
    boolean isRussianSymbol(char c){
        char smallLeft='а',smallRight='я';
        char bigLeft='А',bigRight='Я';
        return ((c>=smallLeft && c<=smallRight) || (c>=bigLeft && c<=bigRight) || c=='ё' || c=='Ё');
    }

    /**
     * Проверка на принадлежность латиницы
     * @param c - символ в тексте
     * @return - истина, если символ латиницы
     */
    boolean isLatinSymbol(char c){
        char smallLeft='a',smallRight='z';
        char bigLeft='A',bigRight='Z';
        return ((c>=smallLeft && c<=smallRight) || (c>=bigLeft && c<=bigRight));
    }

    /**
     * Проверка на принадлежность к арабским цифрам
     * @param c - символ в тексте
     * @return - истина, если символ является арабской цифрой
     */
    boolean isDigit(char c){
        return Character.isDigit(c);
    }


    public static void main(String[] args){
        Scanner scan=new Scanner(System.in);
        // введенные данные должны быть разделены переводом строк
        scan.useDelimiter("\\n");
        String text =scan.next();

        char leftBorder=scan.next().charAt(0);
        char rightBorder=scan.next().charAt(0);
        DemisGroupTask finder=new DemisGroupTask();
        JSONObject stat=finder.getStat(text,leftBorder,rightBorder);
        System.out.println(stat.toString());
    }
}
