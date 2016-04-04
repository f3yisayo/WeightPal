package ninja.feyisayo.apps.weightpal;

public class UserHealth {

    private Long calories_consumed;
    private Long current_weight;
    private String daily_eaten;
    private Long future_weight;
    private Long height;
    private String food_eaten;
    private boolean health_reminder;



    public UserHealth() {
    }

    public Long getHeight() {
        return height;
    }

    public String getFood_eaten() {
        return food_eaten;
    }

    public Long getCurrent_weight() {
        return current_weight;
    }

    public Long getCalories_consumed() {
        return calories_consumed;
    }

    public String getDaily_eaten() {
        return daily_eaten;
    }

    public Long getFuture_weight() {
        return future_weight;
    }

    public boolean isHealth_reminder() {
        return health_reminder;
    }

}
