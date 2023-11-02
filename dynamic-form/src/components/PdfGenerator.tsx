import { renderToString } from 'react-dom/server';
import { PDFRenderer,pdf, BlobProvider,Document, Page, renderToBuffer,Text, View, StyleSheet, Font } from '@react-pdf/renderer';
import { NextResponse,NextRequest } from 'next/server'


type IFont = "Times-Roman" | "Times-Bold" | "Times-Italic" | "Times-BoldItalic";
type ITextDecoration = "line-through" | "underline" | "none" | "line-through underline" | "underline line-through";


function boldItalicValidation(regex:RegExp,line:string,index:number){
  let style: {font:IFont,textDecoration:ITextDecoration,fontSize:string,fontWeigth:IFont} = {
    font: "Times-Roman",
    textDecoration: "none",
    fontSize: "16",
    fontWeigth: "Times-Roman"
  }
  if(line.match(/^[#]{1,6} ([\s\S]*?)/)){
    if(line.match(/^[#]{1} ([\s\S]*?)/)){
      style.fontSize = "32";
      style.fontWeigth = "Times-Bold"
    }else if(line.match(/^[#]{2} ([\s\S]*?)/)){
      style.fontSize = "24";
      style.fontWeigth = "Times-Bold"
    }else if(line.match(/^[#]{3} ([\s\S]*?)/)){
      style.fontSize = "18.72";
      style.fontWeigth = "Times-Bold"
    }else if(line.match(/^[#]{4} ([\s\S]*?)/)){
      style.fontSize = "16";
      style.fontWeigth = "Times-Bold"
    }else if(line.match(/^[#]{5} ([\s\S]*?)/)){
      style.fontSize = "13.28";
      style.fontWeigth = "Times-Bold"
    }else if(line.match(/^[#]{6} ([\s\S]*?)/)){
      style.fontSize = "13";
    }
    line = line.replace(/^[#]{1,6} ([\s\S]*?)/,"$1")
  }
  
    let wordSplit = line.match(regex);
    const arr = []
    if(wordSplit){
      
      console.log(wordSplit)
      for(let i = 0;i < wordSplit.length;i++){
        let word = wordSplit[i];
        if (wordSplit[i].match(/\*\*\*([\s\S]*?)\*\*\*/g)) {
          word = word.replace(/\*\*\*(.*?)\*\*\*/g,"$1");
          style.font = "Times-BoldItalic";
        } else if (word.match(/\*\*([\s\S]*?)\*\*/g)) {
          word = word.replace(/\*\*(.*?)\*\*/g,"$1");
          style.font = "Times-Bold";
        } else if (word.match(/\*([^*]+)\*/g)) {
          word = word.replace(/\*(.*?)\*/g,"$1");
          style.font = "Times-Italic";
        }

        if(word.match(/<u>([\s\S]*?)<\/u>/g)){
          word = word.replace(/<u>(.*?)<\/u>/g,"$1");
          style.textDecoration = "underline";
        }
        arr.push(<Text key={"word-"+i} style={{fontFamily:style.font,textDecoration:style.textDecoration }}>{word}</Text>);
      }
    }
    return <Text key={"line-"+index} style={{fontSize:style.fontSize,fontFamily:style.fontWeigth,display:"flex"}}>{arr}</Text>;
}

export default function PdfGenerator({data}:{data:string[]}) {
  const styles = StyleSheet.create({
      page: {
        flexDirection: "column",
        backgroundColor: "#FFFFFF"
      },
      section: {
        margin: 10,
        padding: 10,
        flexGrow: 1,
        gap: 15
      },
      h1: {
        fontSize: 12,
        textAlign:"center"
      },
      h2: {
        fontSize: 12,
        textAlign:"center",
        marginTop:10
      },
      textRed: {
        color: "red"
      },
      textUppercase: {
        textTransform: "uppercase"
      },
      line: {
        borderTop: 1, 
        borderColor: 'black',
        marginTop: 30,
      },
      p: {
        fontSize: 12,
        marginTop: 10
      },
    });
    return (
        <Document>
            <Page size="A4" style={styles.page}>
                <View style={styles.section}>
                    {data.map((line,index)=>{
                      if(line === "&#x20;"){
                        return
                      }
                      if(line.match(/&#x20;/g)){
                        line = line.replace(/&#x20;/g,"")
                      }
                      let regex = /(?:\*\*\*([\s\S]*?)\*\*\*|\*\*([\s\S]*?)\*\*|\*([\s\S]*?)\*|<u>([\s\S]*?)<\/u>|([^*]+))/g; //GET BOLD AND ITALIC
                      if(line.match(regex)){
                        return boldItalicValidation(regex,line,index);
                      }
                    })}
                </View>
            </Page>
        </Document>
    ); 
}
