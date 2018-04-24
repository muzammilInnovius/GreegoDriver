package com.greegoapp.greegodriver.Model;

public class Login {


    /**
     * data : {"contact_number":"+919998841576","otp":419674,"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6Ijk5YzU0N2Q0ODA2ZGU0YzBlMzk4Zjc4MTZiNTRhNGJjYjdkMGE5MmE4ZTUzMDFmMGMxMTMzMWY1OWRlNzgxNzQyNjNiZGU5MmE3YTY2ZmNlIn0.eyJhdWQiOiIxIiwianRpIjoiOTljNTQ3ZDQ4MDZkZTRjMGUzOThmNzgxNmI1NGE0YmNiN2QwYTkyYThlNTMwMWYwYzExMzMxZjU5ZGU3ODE3NDI2M2JkZTkyYTdhNjZmY2UiLCJpYXQiOjE1MjMzNjM2MjAsIm5iZiI6MTUyMzM2MzYyMCwiZXhwIjoxNTU0ODk5NjIwLCJzdWIiOiI0Iiwic2NvcGVzIjpbXX0.xcPd57y2Et6bzh-YOQMHkmgPyo5Eil5dA7cdl53Xl1yXuTnLbihKiFdQtJ4XewHpOOXuPxPMST_hcwsg0dkN3gd63SF-ewSWrE8umkVki_KxyMEmY1o7eVy5DM7pcpOnDbkSpoW7YAwboQeiyceknYo91b8APIYhbzmxRSO2P8bCnRaoJwwQjP6lgrRGgQ6vsJXLR0CMwwzexNwD_16DIIUzsyB0LGtPc8-q0aLw08aakOiUPrlt8-jEWR0D-APbKo3MlmA-g8wJPYSNRSr8pIP7aYHiRoG9uqO6j-Lh9LMM1-OwHs31SRQnOcIrColqMM7aXReEz6foPbqRLyERQ30bKOsXd7ItF9cRQ8vyo-O5L5WZXpqbyt6-haafARE9fS4b8v4aro-ceXLor2HO7yUXtQaL59na6evQXx2M3xyFILCEGSoRQ2MP_65186p6vGo_JrVpKJsymcS7yu5qNRufMcCIBGJV9CfwWV1lhVVJpXXEEAKyX9BaXWRw642U0ZtiWvxS4pgZ-mB15V99-CE8HGht9BcTNoMjZQ0MBDpPE4TGUopCSrEBClOtNq7rus-7oFYNT7dA-cyggsnF5XJmalULaRHYdPX5KhdZFEyRgFzmhQN5M2dTW-2OIzj7gVy2-UK7B7gm6V5yo749xz3jH0wzuRsPsni-iIhDdcQ"}
     * error_code : 0
     * message :
     */

    private DataBean data;
    private int error_code;
    private String message;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * contact_number : +919998841576
         * otp : 419674
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6Ijk5YzU0N2Q0ODA2ZGU0YzBlMzk4Zjc4MTZiNTRhNGJjYjdkMGE5MmE4ZTUzMDFmMGMxMTMzMWY1OWRlNzgxNzQyNjNiZGU5MmE3YTY2ZmNlIn0.eyJhdWQiOiIxIiwianRpIjoiOTljNTQ3ZDQ4MDZkZTRjMGUzOThmNzgxNmI1NGE0YmNiN2QwYTkyYThlNTMwMWYwYzExMzMxZjU5ZGU3ODE3NDI2M2JkZTkyYTdhNjZmY2UiLCJpYXQiOjE1MjMzNjM2MjAsIm5iZiI6MTUyMzM2MzYyMCwiZXhwIjoxNTU0ODk5NjIwLCJzdWIiOiI0Iiwic2NvcGVzIjpbXX0.xcPd57y2Et6bzh-YOQMHkmgPyo5Eil5dA7cdl53Xl1yXuTnLbihKiFdQtJ4XewHpOOXuPxPMST_hcwsg0dkN3gd63SF-ewSWrE8umkVki_KxyMEmY1o7eVy5DM7pcpOnDbkSpoW7YAwboQeiyceknYo91b8APIYhbzmxRSO2P8bCnRaoJwwQjP6lgrRGgQ6vsJXLR0CMwwzexNwD_16DIIUzsyB0LGtPc8-q0aLw08aakOiUPrlt8-jEWR0D-APbKo3MlmA-g8wJPYSNRSr8pIP7aYHiRoG9uqO6j-Lh9LMM1-OwHs31SRQnOcIrColqMM7aXReEz6foPbqRLyERQ30bKOsXd7ItF9cRQ8vyo-O5L5WZXpqbyt6-haafARE9fS4b8v4aro-ceXLor2HO7yUXtQaL59na6evQXx2M3xyFILCEGSoRQ2MP_65186p6vGo_JrVpKJsymcS7yu5qNRufMcCIBGJV9CfwWV1lhVVJpXXEEAKyX9BaXWRw642U0ZtiWvxS4pgZ-mB15V99-CE8HGht9BcTNoMjZQ0MBDpPE4TGUopCSrEBClOtNq7rus-7oFYNT7dA-cyggsnF5XJmalULaRHYdPX5KhdZFEyRgFzmhQN5M2dTW-2OIzj7gVy2-UK7B7gm6V5yo749xz3jH0wzuRsPsni-iIhDdcQ
         */

        private String contact_number;
        private int otp;
        private String token;

        public String getContact_number() {
            return contact_number;
        }

        public void setContact_number(String contact_number) {
            this.contact_number = contact_number;
        }

        public int getOtp() {
            return otp;
        }

        public void setOtp(int otp) {
            this.otp = otp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
