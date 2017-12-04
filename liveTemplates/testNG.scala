import org.testng.annotations._
  
@DataProvider(name = "$name$Provider")
def $name$ProviderProvider(): Array[Array[AnyRef]] = {
Array(Array("iput", "output"))

}

@Test(dataProvider = "$name$Provider")
def  $name$Test(iput: String, output: String): Unit = {
}