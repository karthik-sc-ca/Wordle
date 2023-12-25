package com.kcsreens;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Wordle {

    public List<String> getWordsFromStream(InputStream inputStream) {
        List<String> words = new ArrayList<>();
        try (Scanner scanner = new Scanner(inputStream).useDelimiter(",")) {
            while (scanner.hasNext()) {
                String str = scanner.next();
                words.add(str);
            }
        }
        return words;
    }

    public List<String> getWordles() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream("wordle.txt"));
        return getWordsFromStream(inputStream);
    }

    public List<String> getNonWordles() {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream("nonwordle.txt"));
        return getWordsFromStream(inputStream);
    }

    public List<String> getAllGameWords() {
        List<String> wordles = getWordles();
        List<String> nonWordles = getNonWordles();
        List<String> allWords = new ArrayList<>(wordles);
        allWords.addAll(nonWordles);
        return allWords;
    }

    private String getStarterWord() {
        String[] starters = {"oater", "realo", "alert", "irate", "ariel", "aeros", "aster", "earls", "arise", "antre"};
        return getRandom(Arrays.asList(starters));
    }

    private List<String> filterWords(List<String> words, final Guess[] guess) {
        return words.stream().filter(word -> {
            Set<Character> wordSet = word.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
            for (Guess g : guess) {
                if (g.g == Match.IN_POS && word.charAt(g.i) != g.c) {
                    return false;
                } else if (g.g == Match.IN_POS) {
                    wordSet.remove(g.c);
                }
            }
            for (Guess g : guess)
            {
                if (g.g == Match.MISSING && wordSet.contains(g.c))
                {
                    return false;
                }
                if (g.g == Match.OUT_OF_POS && !wordSet.contains(g.c))
                {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private String getRandom(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private boolean isCorrectGuess(Guess[] guess) {
        return Arrays.stream(guess).allMatch(g -> g.g == Match.IN_POS);
    }

    private Guess[] matchGuess(String guess, String answer) {
        if (guess.length() != answer.length()) {
            System.out.println(guess + "," + answer);
            return null;
        }

        Guess[] result = new Guess[guess.length()];
        List<Character> guessCharList = guess.chars().mapToObj(n -> (char) n).toList();
        List<Character> answerCharList = answer.chars().mapToObj(n -> (char) n).toList();

        Set<Character> answerCharSet = new HashSet<>(answerCharList);
        for (int i = 0; i < answer.length(); i++)
        {
            result[i] = new Guess(i);
            result[i].c = guessCharList.get(i);
            if (guessCharList.get(i) == answerCharList.get(i))
            {
                result[i].g = Match.IN_POS;
                answerCharSet.remove(answerCharList.get(i));
            } else
            {
                result[i].g = Match.UNSET;
            }
        }
        for (int i = 0; i < answer.length(); i++)
        {
            if (result[i].g == Match.UNSET)
            {
                if (answerCharSet.contains(guessCharList.get(i)))
                {
                    result[i].g = Match.OUT_OF_POS;
                } else
                {
                    result[i].g = Match.MISSING;
                }

            }
        }
        return result;
    }

    public List<Guess[]> guessMatch(String answer) {
        List<Guess[]> guesses = new ArrayList<>();
        Guess[] g = matchGuess(getStarterWord(), answer);
        List<String> words = getAllGameWords();
        guesses.add(g);

        while (!isCorrectGuess(g)) {
            words = filterWords(words, g);
            g = matchGuess(getRandom(words), answer);
            guesses.add(g);
        }

        return guesses;
    }

    public static void main(String[] args) {
        Wordle wordle = new Wordle();
        Map<String, List<Guess[]>> map = new HashMap<>();

        List<String> wordles = wordle.getWordles();
        for (String answer : wordles) {
            List<Guess[]> list = wordle.guessMatch(answer);
            map.put(answer, list);
        }

        float average = 0f;
        for (Map.Entry<String, List<Guess[]>> e : map.entrySet()) {
            average += e.getValue().size();
            System.out.println(e.getKey() + "->" + e.getValue().size());
        }

        map.forEach((k, v) -> {
            System.out.println(k + "->");
            v.forEach(g -> System.out.println("   " + Arrays.toString(g)));
        });

        System.out.println("Total chances:" + average);
        System.out.println("Total 5 letter Words in game:" + wordle.getAllGameWords().size());
        System.out.println("Total 5 letter Wordles in game:" + map.size());
        average = average / (float) map.size();
        System.out.println("Average Chances:" + average);
    }

}
