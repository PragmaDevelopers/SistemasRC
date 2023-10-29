import { Document, Page, Text, Image, View, Link,StyleSheet } from "@react-pdf/renderer";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";
import { useEffect, useState } from "react";
import fs from "fs"

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

interface ITable {
  text:{
    style: object,
    value: string
  }
}

function PdfGenerator({data}:{data:string}) {
    const [table,setTable] = useState<ITable[]>([]);
    
    useEffect(()=>{
      
    },[])

    return (
        <Document>
            <Page size="A4" style={{...styles.page,padding:10}}>
              {table?.map((row:any)=>{
                return (
                  <View key={row.text.value} style={{marginBottom:20}}>
                    <Text style={row.text.style}>{row.text.value}</Text>
                  </View>
                )
              })}
              <Link src="" id="oi">Listar console</Link>
            </Page>
        </Document>
    ); 
}

export default PdfGenerator;