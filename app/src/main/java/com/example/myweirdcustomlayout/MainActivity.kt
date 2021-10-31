package com.example.myweirdcustomlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myweirdcustomlayout.ui.theme.MyWeirdCustomLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeirdCustomLayoutTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MyItemsContent()
                }
            }
        }
    }
}

@Composable
private fun MyWeirdLayout(
    modifier: Modifier = Modifier,
    rows: Int = 10,
    content: @Composable () -> Unit
){
    Layout(content = content, modifier = modifier ){ measurables, constrainsts->
        // keeping track of the w/h of every item in order to use layout size
        var layoutWidth = 0
        var layoutHeight = 0

        // keep track of the occurrence of the the second element in order
        // to stop counting the height
        var occurence = 0

        val placeables = measurables.mapIndexed{ index, measurable ->
            val placeable = measurable.measure(constrainsts)

            // make the layout as wide as the sum of items' width
            layoutWidth += placeable.width
            // if the second item recurred then we will stop counting the height
            // which means that the layout will equal to the sum of each item height
            if(index%rows==1){
                if(occurence<2)
                    occurence++
            }
            layoutHeight  += if(occurence!=2) placeable.height else 0

            placeable

        }

        layout(layoutWidth, (layoutHeight)){
            var rowItemX = 0
            var rowItemY = 0

            // making an indicator of the items if it reached the bottom of the screen
            // in order to reflect the pattern vertically and repeatedly
            var shouldReflect = false
            placeables.forEachIndexed{index, placeable ->
                if(!shouldReflect){
                    placeable.placeRelative(rowItemX, rowItemY)
                    rowItemX+= placeable.width
                    rowItemY+=placeable.height
                    shouldReflect = if(index%rows==rows-1) !shouldReflect else false
                }else{
                    placeable.placeRelative(rowItemX, rowItemY)
                    rowItemX+= placeable.width
                    rowItemY-=placeable.height
                    shouldReflect = if(index%rows==rows-1) !shouldReflect else true
                }

            }

        }
    }
}

@Composable
private fun ChipItem(itemText: String){
    Card(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(20),
        border = BorderStroke(1.dp,MaterialTheme.colors.secondary)
    ) {
        Text(text = itemText, modifier = Modifier.padding(3.dp), color = MaterialTheme.colors.secondary)
    }
}

@Composable
private fun MyItemsContent(listOfItems: List<String> = List(1000) { "item$it" }) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            MyWeirdLayout(rows = 15) {
                for (item in listOfItems) {
                    ChipItem(itemText = item)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChipItemDefaultPreview() {
    MyWeirdCustomLayoutTheme {
        ChipItem("item 1")
    }
}

@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    MyWeirdCustomLayoutTheme {
        MyItemsContent()
    }
}