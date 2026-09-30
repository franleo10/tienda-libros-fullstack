package org.utn.tpfinalprogramacion3.dtos;

public class WebhookDTO {

    private String type;
    private Data data;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public static class Data {
        private Long id;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

}
