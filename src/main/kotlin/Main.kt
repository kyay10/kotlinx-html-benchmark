package main

import java.util.*
import kotlinx.benchmark.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML

// Domain class
data class Product(val name: String, val price: Int)

// Data
val products =
    (1..5000).map { Product(UUID.randomUUID().toString(), (Math.random() * 100000).toInt()) }

@State(Scope.Benchmark)
class KotlinxHtmlBenchmark {
  @Param("true", "false") var isSingleProductBenchmark: Boolean = false
  @Benchmark
  fun renderWithProductComponent1(blackhole: Blackhole) {
    withDivAndProducts(blackhole) { productComponent1(it)() }
  }

  @Benchmark
  fun renderWithProductComponent2(blackhole: Blackhole) {
    withDivAndProducts(blackhole) { consumer.productComponent2(it) }
  }

  @Benchmark
  fun renderWithProductComponent3(blackhole: Blackhole) {
    withDivAndProducts(blackhole) { productComponent3(it) }
  }

  final inline fun withDivAndProducts(
      blackhole: Blackhole,
      crossinline block: DIV.(Product) -> Unit
  ) =
      blackhole.consume(
          createHTML().div {
            if (isSingleProductBenchmark) {
              block(products.first())
            } else {
              for (product in products) {
                block(product)
              }
            }
          })
}

// Render using returned lambda receiver
fun productComponent1(product: Product): FlowContent.() -> Unit = {
  div {
    h5 { +product.name }
    div { +"$ ${product.price}" }
  }
}

// Render using TagConsumer extension function
fun TagConsumer<*>.productComponent2(product: Product) {
  div {
    h5 { +product.name }
    div { +"$ ${product.price}" }
  }
}

// Render using FlowContent/Tag extension function
fun FlowContent.productComponent3(product: Product) {
  div {
    h5 { +product.name }
    div { +"$ ${product.price}" }
  }
}
