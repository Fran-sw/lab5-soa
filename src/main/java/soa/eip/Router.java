package soa.eip;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
      .log("Body contains \"${body}\"")
      .log("Searching twitter for \"${body}\"!")
      .process(new CustomProcessor())
      .log("${body}")
      .toD("twitter-search:${body}")
      .log("Body now contains the response from twitter:\n${body}");
  }

  public class CustomProcessor implements Processor{
    public void process(Exchange exchange) throws Exception{

      //Get the msg
      Message msg = exchange.getIn();
      String msg_txt = msg.getBody(String.class);

      if(msg_txt.matches("^[a-zA-Z0-9]+ max:[0-9]+$")){
        //Need to get the max value -> search for :
        //We will get a string like this "***** max:n -> {****** max}{n}
        String split[] = msg_txt.split(":");
        msg_txt = msg_txt.replace("max:"+Integer.parseInt(split[1]),"");
        msg_txt = msg_txt + " ?count=" + Integer.parseInt(split[1]);
      }
      exchange.getOut().setBody(msg_txt);
    }
  }
}
