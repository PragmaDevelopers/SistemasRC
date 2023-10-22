"use client";

import { PDFViewer } from "@react-pdf/renderer";
import Trabalhista from "./components/power_of_attorney/Trabalhista";
import { useEffect, useState } from "react";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";

function PDFPage() {
  const [height,setHeight] = useState(600);
  const [signUpData,setSignUpData] = useState<IFormSignUpInputs>();

  useEffect(()=>{
    setHeight(window.innerHeight)
    const sessionData = sessionStorage.getItem("registration_form");
    if(sessionData){
      setSignUpData(JSON.parse(sessionData))
    }
  },[])
  return (
    <PDFViewer width="100%" height={height}>
      {
        signUpData && <Trabalhista data={signUpData} />
      }
    </PDFViewer>
  );
}

export default PDFPage;