package com.kcsreens;

import java.util.*;
import java.util.stream.Collectors;

public class Wordle
{


    List<String> getWordles()
    {
        List<String> listOfStrings = new ArrayList<>();
        Scanner sc = new Scanner(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("wordle.txt"))).useDelimiter(",");
        String str;
        while (sc.hasNext()) {
            str = sc.next();
            listOfStrings.add(str);
        }
        return listOfStrings;
    }

    List<String> getNonWordles()

    {
        List<String> listOfStrings = new ArrayList<>();
        Scanner sc = new Scanner(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("nonwordle.txt"))).useDelimiter(",");
        String str;
        while (sc.hasNext()) {
            str = sc.next();
            listOfStrings.add(str);
        }
        return listOfStrings;
    }

    List<String> getAllGameWords() {
        return new ArrayList<>() {
            {
                addAll(getWordles());
                addAll(getNonWordles());
            }
        };
    }

    String getStarterWord()
    {
        String[] starters =
                { "oater", "realo", "alert", "irate", "ariel", "aeros", "aster", "earls", "arise", "antre" };
        return getRandom(Arrays.asList(starters));
    }

    List<String> filter(List<String> list, final Guess[] guess)
    {
        return list.stream().filter(s -> {
            Set<Character> sset = s.chars().mapToObj(n -> (char) n).collect(Collectors.toCollection(HashSet::new));
            for (Guess g : guess)
            {
                if (g.g == Match.IN_POS)
                {
                    if (s.charAt(g.i) != g.c)
                    {
                        return false;
                    } else
                    {
                        sset.remove(g.c);
                    }
                }
            }
            for (Guess g : guess)
            {
                if (g.g == Match.MISSING && sset.contains(g.c))
                {
                    return false;
                }
                if (g.g == Match.OUT_OF_POS && !sset.contains(g.c))
                {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    String getRandom(List<String> list)
    {
        return list.get(new Random().nextInt(list.size()));
    }

    List<Guess[]> guessMatch(String answer) {
        List<Guess[]> ret = new ArrayList<>();
        Guess[] g = matchGuess(getStarterWord(), answer);
        List<String> list = getAllGameWords();
        ret.add(g);
        while (!correctGuess(g))
        {
            list = filter(list, g);
            g = matchGuess(getRandom(list), answer);
            ret.add(g);
        }
        return ret;
    }



    public static void main(String[] args) {

        Wordle wordle = new Wordle();

        Map<String, List<Guess[]>> map = new HashMap<>();
        for (String answer : wordle.getWordles())
        {
            List<Guess[]> list = wordle.guessMatch(answer);
            map.put(answer, list);
        }

        float average = 0f;
        for (Map.Entry<String, List<Guess[]>> e : map.entrySet())
        {
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

    enum Match
    {
        UNSET("WHITE"), IN_POS("GREEN"), OUT_OF_POS("YELLOW"), MISSING("GREY");

        private final String color;

        Match(String color)
        {
            this.color = color;
        }

        @Override
        public String toString()
        {
            return color;
        }

    }

    static class Guess
    {
        Character c;
        Match g;
        int i;

        Guess(int index)
        {
            this.i = index;
        }

        @Override
        public String toString()
        {
            return c + "->" + g;
        }
    }

    boolean correctGuess(Guess[] guess)
    {
        return Arrays.stream(guess).allMatch(g -> g.g == Match.IN_POS);
    }

    Guess[] matchGuess(String guess, String answer)
    {
        if (guess.length() != answer.length())
        {
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

}
