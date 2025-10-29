package org.challenge6.javaspark;

import static spark.Spark.get;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
        public static void main(String[] args) {

            get("/hello", (req, res)->"Hello, world");

            get("/hello/:name", (req,res)->{
                return "Hello, "+ req.params(":name");
            });
        }

}