import { Document, Page, Text, View, StyleSheet } from "@react-pdf/renderer";
import { useEffect, useState } from "react";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";

const styles = StyleSheet.create({
  page: {
    flexDirection: "column",
    backgroundColor: "#FFFFFF",
  },
  section: {
    margin: 10,
    padding: 10,
    flexGrow: 1,
  },
  text: {
    fontSize: 12,
  },
});

function PdfGenerator() {
    const [data,setData] = useState<IFormSignUpInputs>();
    useEffect(()=>{
        const sessionData = sessionStorage.getItem("registration_form");
        if(sessionData){
            setData(JSON.parse(sessionData))
        }
    },[])
    return (
        <Document>
            <Page size="A4" style={styles.page}>
                <View style={styles.section}>
                    <Text style={{fontSize: 12,marginBottom: 10}}>CONTRATO DE PRESTAÇÃO DE SERVIÇOS</Text>
                    <Text style={styles.text}>CONTRATANTE – {data?.full_name}, {data?.nationality}, {data?.marital_status}, {Boolean(data?.common_law_marriage) && "não"} vive em união estável, {data?.ocuppation}, portador da carteira de identidade nº {data?.rg} {data?.issuing_body}/{data?.uf_for_RG}, inscrito no CPF sob nº {data?.cpf}, filho de {data?.mother_name}, endereço eletrônico: {data?.email} residente e domiciliado à {data?.address_name}, {data?.address_complement_name}, {data?.neighborhood}, {data?.city}, {data?.state_for_address}, CEP: {data?.cep}.</Text>
                </View>
            </Page>
        </Document>
    ); 
}

export default PdfGenerator;