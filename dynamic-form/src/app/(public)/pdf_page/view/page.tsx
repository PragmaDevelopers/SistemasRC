"use client";

import { PDFViewer } from "@react-pdf/renderer";
import PdfGenerator from "@/components/PdfGenerator";
import { useEffect, useState } from "react";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";

function PDFPage() {
  const [height,setHeight] = useState(600);
  const [pdfInfo,setPdfInfo] = useState<string>();
  useEffect(()=>{
    setHeight(window.innerHeight)
    const pdfInfoDataSession = sessionStorage.getItem("pdf_info");
    if(pdfInfoDataSession){
      setPdfInfo(JSON.parse(pdfInfoDataSession))
    }
  },[])
  return (
    pdfInfo ? 
      <PDFViewer width="100%" height={height}>
        <PdfGenerator data={pdfInfo} />
      </PDFViewer> 
    : 
    <div>Erro no pdf!</div>
  );
}

export default PDFPage;