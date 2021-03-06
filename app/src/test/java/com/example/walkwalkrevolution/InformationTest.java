package com.example.walkwalkrevolution;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.walkwalkrevolution.Fitness.FitnessService;
import com.example.walkwalkrevolution.Fitness.FitnessServiceFactory;
import com.example.walkwalkrevolution.ui.information.InformationFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertEquals;

@RunWith(value = AndroidJUnit4.class)
public class InformationTest {
    private static final String TEST_SERVICE = "TEST_SERVICE";

    private Intent intent;
    private long nextStepCount;
    private long nextMile;
    private AppCompatActivity walkActivity;
    private InformationFragment infoFragment;
    private Button stopButton;

    @Before
    public void setUp() {
        FitnessServiceFactory.put(TEST_SERVICE, TestFitnessService::new);
        intent = new Intent(ApplicationProvider.getApplicationContext(), WalkInProgress.class);
        intent.putExtra(WalkInProgress.FITNESS_SERVICE_KEY, TEST_SERVICE);

        SharedPreferences sharedPreferences =
                InstrumentationRegistry.getInstrumentation().getContext().getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("height", "65");
        editor.apply();

        //going into the new walk or route
        ActivityScenario<WalkInProgress> scenario = ActivityScenario.launch(intent);
        scenario.onActivity(activity -> {
            walkActivity = activity;
            stopButton = activity.findViewById(R.id.btn_STOP);
            stopButton.performClick();
            infoFragment =
                    (InformationFragment) walkActivity.getSupportFragmentManager().findFragmentByTag("INFO FRAG");
        });
    }

    @Test
    public void stopButtonRemoved(){
        assertEquals(View.GONE, stopButton.getVisibility());
    }

    @Test
    public void informationFragmentWasCreated(){
        assertEquals(false, infoFragment == null);
    }

    @Test
    public void noInformationEntered(){
        assertEquals(false, infoFragment.allowedToBeDone());
    }

    @Test
    public void requiredInformationEntered(){
        EditText routeName = infoFragment.getView().findViewById(R.id.editText_route_name);
        routeName.setText("TEST ROUTE");
        assertEquals(true, infoFragment.allowedToBeDone());
    }

    private class TestFitnessService implements FitnessService {
        private static final String TAG = "[TestFitnessService]: ";
        private WalkInProgress stepCountActivity;

        public TestFitnessService(WalkInProgress stepCountActivity) {
            this.stepCountActivity = stepCountActivity;
        }

        @Override
        public int getRequestCode() {
            return 0;
        }

        @Override
        public void setup() {
            System.out.println(TAG + "setup");
        }

        @Override
        public void updateStepCount() {
            System.out.println(TAG + "updateStepCount");
            stepCountActivity.setStepCount(nextStepCount);
        }

    }

}
