package org.example;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
public class Main {
    private static WebDriver driver;
    private static int implicitWaitInSecond=5000;
    private static void waitForVisibility(By by){
        WebDriverWait wait = new WebDriverWait(driver,implicitWaitInSecond/1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    public static void main(String[] args) throws InterruptedException {
        int numberOfCivic=0;

        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        driver=new ChromeDriver();
        driver.navigate().to("https://www.roimotors.com/");

        List<WebElement> dropdownList=driver.findElements(By.className("right-border"));

        for(int i=0;i< dropdownList.size()-1;i++)
        {
            dropdownList.get(i).click();

            List<WebElement> options= driver.findElements(By.cssSelector(".select-options>ul>li"));

            //i==0 indicates first dropdown
            //i==1 indicates second dropdown
            //i==2 indicates third dropdown
            if(i==0)
            {
                //select New
                options.get(1).click();
            } else if (i==1) {
                //select 2023
                options.get(1).click();
            } else if (i==2) {
                waitForVisibility(By.xpath("//div[@id='new-facet-browse']//div[@class='facet-list']"));
                List<WebElement> facetList= driver.findElements(By.xpath("//div[@id='new-facet-browse']//div[@class='facet-list']"));
                //select Model
                facetList.get(3).click();
                waitForVisibility(By.cssSelector(".facet-list-group>div>ol>li"));
                WebElement civicWebElement= driver.findElement(By.xpath("//span[@class='facet-list-facet-label-text' and text()='Civic']"));
                //select Civic
                civicWebElement.click();
                //getting number of civic
                WebElement sibling = civicWebElement.findElement(By.xpath("following-sibling::*"));
                numberOfCivic=Integer.parseInt(sibling.getText());
                System.out.println("Number of Civic: "+ numberOfCivic);
                //click on search button
                driver.findElement(By.cssSelector(".p-0.modal-footer")).click();

                waitForVisibility(By.id("inventory-filters1-app-root"));
                List<WebElement> spanList =  driver.findElements(By.cssSelector(".my-1>strong>span"));

                String searchResultText = spanList.get(0).getAttribute("innerHTML");
                String[] splitedSearchResult= (searchResultText.split(" ",2));
                int numberOfCivicInDescription=Integer.parseInt(splitedSearchResult[0]);

                System.out.println("Number of Civic in description: "+numberOfCivicInDescription);

                if (numberOfCivicInDescription==numberOfCivic)
                {
                    System.out.println("Passed!!");
                }
            }
        }
    }
}