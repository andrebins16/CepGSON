package com.example;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient; 
import java.net.http.HttpRequest; 
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;
import static java.time.temporal.ChronoUnit.MINUTES;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Menu{

    private Scanner in;
    private static Menu instance;
    private final String urlBase="https://viacep.com.br/ws/";
    private Gson gson;

    private Menu() {
        gson= new Gson();
        in = new Scanner(System.in);
    }

    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
            return instance;
        } else {
            return instance;
        }
    }

    public void loop(){
        clearConsole();
        int choice = 0;
        while (choice != 3) {
            System.out.println("(1) Validar CEP");
            System.out.println("(2) Pesquisar CEP");
            System.out.println("(3) Encerrar consulta");
            choice = getUserChoice(3);
            handleChoice(choice);
        }
    }
 
    public  int getUserChoice(int choiceOptions) {
        int choice = 0;
        try {
            choice = in.nextInt();
            in.nextLine();
            if (!(choice >= 1 && choice <= choiceOptions)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Por favor, informe uma opção válida.");
            in.nextLine();
        }
        return choice;
    }

    public  void handleChoice(int choice){
        switch(choice){
            case 1:
                valCEP();
                break;
            case 2:
                searchCEP();
                break;
            case 3:
                choice = 3;
                break;
        }
    }

    public  void valCEP(){
        
        System.out.println("Digite o cep a ser pesquisado");
        String cep= in.nextLine();
        String url=urlBase+cep+"/json/";

        try { 
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.of(1, MINUTES)).build(); 
            HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            try {
                Cep cepObject=gson.fromJson(httpResponse.body().toString(), Cep.class);
                if(cepObject.isNull()){
                    clearConsole();
                    System.out.println("Cep inexistente. Tente novamente!");
                    valCEP();
                }else{
                    clearConsole();
                    System.out.println(cepObject);
                }

            } catch (JsonSyntaxException e) {
                clearConsole();
                System.out.println("Formato do cep inválido. Tente novamente!");
                     valCEP();
            }

        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException(e.getMessage()); 
        } catch (InterruptedException e) {
            e.printStackTrace(); 
            throw new RuntimeException(e.getMessage()); }

         System.out.println("\nSe você deseja retornar ao menu digite 0, se deseja validar novamente digite 1!");
         int op = in.nextInt();
         in.nextLine();
         if (op == 1) {
            clearConsole();
            valCEP();
         } else {
            loop();
        }
    }

    public  void searchCEP(){
        
        System.out.println("Digite o estado a ser pesquisado");
        String estado= in.nextLine();
        estado=estado.replace(" ", "%20");

        System.out.println("Digite a cidade a ser pesquisada");
        String cidade= in.nextLine();
        cidade=cidade.replace(" ", "%20");

        System.out.println("Digite o logradouro a ser pesquisado");
        String logradouro= in.nextLine();
        logradouro=logradouro.replace(" ", "%20");

        try { 
            String url =urlBase+estado+"/"+cidade+"/"+logradouro+"/json/";
    
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.of(1,MINUTES)).build(); 
            HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            //System.out.println(httpResponse.body());
            try {

                Cep [] cepObjects= gson.fromJson(httpResponse.body().toString(), Cep[].class);
                clearConsole();
                for(Cep cepObj: cepObjects){
                    System.out.println(cepObj+"\n");
                }
                
            } catch (JsonSyntaxException e) {
                clearConsole();
                System.out.println("Dados inválidos. Tente novamente!");
                searchCEP();
            }

            } catch (IOException e) {
                e.printStackTrace(); 
                throw new RuntimeException(e.getMessage()); 
            } catch (InterruptedException e) {
                e.printStackTrace(); 
                throw new RuntimeException(e.getMessage());
            } 
                
         System.out.println("\nSe você deseja retornar ao menu digite 0, se deseja validar novamente digite 1!");
         int op = in.nextInt();
         in.nextLine();
         if (op == 1) {
            clearConsole();
            searchCEP();
         } else {
            loop();
        }
    }

    public  void clearConsole() {
        for (int i = 0; i < 50; ++i){
            System.out.println();
        }
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}



