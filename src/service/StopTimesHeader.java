package service;

public enum StopTimesHeader {
    trip_id(0), arrival_time(1), departure_time(2), stop_id(3), stop_sequence(4),timepoint(9);

    private final int index;

    StopTimesHeader(int n){
        this.index = n;
    }

    public int getIndex(){
        return this.index;
    }


}
