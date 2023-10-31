import { renderToString } from 'react-dom/server';
import { PDFRenderer,pdf, BlobProvider,Document, Page, renderToBuffer,Text, View, StyleSheet, Font } from '@react-pdf/renderer';
import { NextResponse,NextRequest } from 'next/server'

function boldItalicUnderLineAlignValidation(regex:RegExp,line:string,index:number,trueValue:string,falseValue:string){
    let wordSplit = (" "+line).split(regex);
    const arr = []
    let isActive = true;
    for(let i = 2;i < wordSplit.length;i+=2){
        if(wordSplit[i + 1]){
          arr.push(<Text key={"word-"+i} style={{fontFamily: isActive ? trueValue : falseValue}}>{wordSplit[i]+wordSplit[i + 1]}</Text>);
        }
        if(isActive){
          isActive = false;
        }else{
          isActive = true;
        }
    }
    return <Text key={"line-"+index} style={{display:"flex"}}>{arr}</Text>;
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
        flexGrow: 1
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
                      let regex = /([^\\])\*\*([^\*]*)([^\\])\*\*/g; //GET BOLD
                        if((" "+line).match(regex)){
                          return boldItalicUnderLineAlignValidation(/([^\\])\*\*([^\*]*)([^\\])\*\*/g,line,index,"Times-Bold","Times-Roman");
                        }
                        regex = /([^\\])\*([^\*]*)([^\\])\*/g; //GET ITALIC
                        if((" "+line).match(regex)){
                          return boldItalicUnderLineAlignValidation(regex,line,index,"Times-Italic","Times-Roman");
                        }
                        regex = /([^\\])\*\*\*([^\*]*)([^\\])\*\*\*/g; //GET BOLD AND ITALIC
                        if((" "+line).match(regex)){
                          return boldItalicUnderLineAlignValidation(regex,line,index,"Times-BoldItalic","Times-Roman");
                        }
                        //IF IS &#x20;
                        return <Text key={"line-"+index} style={{marginTop: 5,marginBottom:5}}></Text>;
                    })}
                </View>
            </Page>
        </Document>
    ); 
}
