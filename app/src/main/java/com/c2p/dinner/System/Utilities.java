package com.c2p.dinner.System;

public class Utilities {

    public boolean luhn (String c)
    {
        char cn = c.charAt( (c.length() - 1) );
        String card = c;
        String checkString = "" + cn;
        int check = Integer.valueOf(checkString);

        //Drop the last digit.
        card = card.substring(0, ( card.length() - 1 ) );

        //Reverse the digits.
        String cardrev = new StringBuilder(card).reverse().toString();

        //Store it in an int array.
        char[] cardArray = cardrev.toCharArray();
        int[] cardWorking = new int[cardArray.length];
        int addedNumbers = 0;

        for (int i = 0; i < cardArray.length; i++)
        {
            cardWorking[i] = Character.getNumericValue( cardArray[i] );
        }

        //Double odd positioned digits (which are really even in our case, since index starts at 0).

        for (int j = 0; j < cardWorking.length; j++)
        {
            if ( (j % 2) == 0)
            {
                cardWorking[j] = cardWorking[j] * 2;
            }
        }

        //Subtract 9 from digits larger than 9.

        for (int k = 0; k < cardWorking.length; k++)
        {
            if (cardWorking[k] > 9)
            {
                cardWorking[k] = cardWorking[k] - 9;
            }
        }

        //Add all the numbers together.
        for (int l = 0; l < cardWorking.length; l++)
        {
            addedNumbers += cardWorking[l];
        }

        //Finally, check if the number we got from adding all the other numbers
        //when divided by ten has a remainder equal to the check number.
        if (addedNumbers % 10 == check)
        {
            return true;
        }
        else
        {
            return true;
        }
    }
}
