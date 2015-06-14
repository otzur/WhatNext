package learn2crack.chat;

/**
 * Created by otzur on 6/3/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        OptionFragment optionFragment = new OptionFragment();
        Bundle bundl = new Bundle();

        switch (index) {
            case 0:
            {
                // Top Rated fragment activity
                bundl.putInt("numberOfOptions", 2);
            }break;

            case 1:
            {
                // Games fragment activity
                bundl.putInt("numberOfOptions", 5);
            }break;

            case 2:
            {
                // Movies fragment activity
                bundl.putInt("numberOfOptions", 8);
            }break;
        }

        optionFragment.setArguments(bundl);
        return optionFragment;
        //return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
}