package br.tcc;

public class Analytics {

    public double size_payload_request;
    public double size_payload_response;
    public double time_elapsed;

    public String query_name;

    public void setQuery_name(String query_name){
        this.query_name = query_name;
    }

    @Override
    public String toString() {
        return "Analytics " + query_name + " {" +
                "size_payload_request=" + size_payload_request +
                ", size_payload_response=" + size_payload_response +
                ", time_elapsed=" + time_elapsed +
                '}';
    }
}
