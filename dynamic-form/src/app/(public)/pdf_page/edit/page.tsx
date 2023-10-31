"use client";

import { LegacyRef, MutableRefObject, ReactEventHandler, Ref, SyntheticEvent, useEffect, useRef, useState } from "react";
import { IFormSignUpInputs } from "@/Interface/IFormInputs";
import { useRouter } from "next/navigation";
const Mustache = require('mustache');

import '@mdxeditor/editor/style.css'
import { jsxPlugin,MDXEditor,quotePlugin,headingsPlugin,tablePlugin,InsertTable,MDXEditorMethods,BlockTypeSelect,UndoRedo,BoldItalicUnderlineToggles,toolbarPlugin } from "@mdxeditor/editor";

function PDFPageEdit() {


  const [signUpData,setSignUpData] = useState<IFormSignUpInputs>();
  const [variable,setVariable] = useState<string>("");
  const [paragraphNumber,setParagraphNumber] = useState<number>(1);
  const [textArea,setTextArea] = useState("");

  const [globalSelect,setGlobalSelect] = useState<{
    selectionText:string,start:number,end:number,
    targetElement:HTMLElement | null | undefined}>
    ({
    selectionText: "",
    start: 0,
    end: 0,
    targetElement: null
  });

  const ref = useRef<MDXEditorMethods>(null)

  useEffect(()=>{
    const sessionData = sessionStorage.getItem("registration_form");
    if(sessionData){
      setSignUpData(JSON.parse(sessionData))
    }

  },[])

  function selectList(list:object | undefined){
    if(list){
        const arr = []
        for(const item in list){
            arr.push(item)
        }
        return arr;
    }else{
        return [""];
    }
  }
  
  function addVariable(){
    const lines = ref.current?.getMarkdown().split("\n\n");
    const lineIndex = globalSelect.targetElement?.id
    if(lines && lineIndex){
      const line = lines[Number(lineIndex)];
      let replacement = ""
      if(!["","&#x20;",undefined].includes(line)){
        const filterLine = line.trim().replace(/\\/g,"").replace(/&#x20;/g," ");
        let spaceEndLine = globalSelect.start - filterLine.split("").length;
        if(spaceEndLine < 0){
          spaceEndLine = 0;
        }
        replacement = filterLine.split("").map((char,index,arr)=>{
          if(globalSelect?.end - spaceEndLine === arr.length && index === arr.length - 1){
              char = char + variable;
          }else if(index - 1 === globalSelect?.start - 1 && index === globalSelect?.end){
            char = variable + char;
          }
          return char;
        }).join("");
      }else if(["","&#x20;"].includes(line) || !line){
        replacement = variable;
      }
      lines.splice(Number(lineIndex),1,replacement);
      const formattedLines = spaceVerification(lines);
      ref.current?.setMarkdown(formattedLines.join("\n\n"));
    }
  }

  function spaceVerification(arr:string[]){
    const newArr:string[] = [];
        arr.forEach((line)=>{
          if(line === ""){
            newArr.push("&#x20;");
          }else{
            newArr.push(line);
          }
        });
      return newArr;
  }

  function formSubmit(){
    // const router = useRouter();
    let textToPdf = ref.current?.getMarkdown().replace(/\\/g,"") || "";
    const filterTextToPdf = spaceVerification(textToPdf.split("\n\n"));
    if(filterTextToPdf[0] !== "&#x20;"){
      filterTextToPdf.unshift("&#x20;");
    }
    var output = Mustache.render(filterTextToPdf.join("\n\n"),signUpData);
    console.log(output)
    sessionStorage.setItem("pdf_info",JSON.stringify(output));
    // router.push("./view");
  }
  return (
    <div className="mx-auto w-full max-w-5xl">
        <h1>Editor de pdf:</h1>
        <div className="flex justify-between">
            <div>
                <select className="bg-slate-400 p-2 rounded-md me-2" onChange={(e)=>setVariable(e.target.value)}>
                    { selectList(signUpData).map((option)=>{
                        return <option key={option} value={`{{${option}}}`}>{option}</option>
                    }) }
                </select>
                <button onClick={()=>{addVariable()}} className="bg-slate-400 p-2 rounded-md" type="button">Adicionar Variável</button>
            </div>
            <button onClick={()=>formSubmit()} type="button" className="bg-slate-400 p-2 rounded-md">Criar PDF</button>
        </div>
        <div onSelect={(e)=>{
            const getSelection = window.getSelection();
            if(getSelection){
              const allElements = e.currentTarget.children[0].children[1].children[0].children;
              for(let i = 0;i < allElements.length;i++){
                allElements[i].id = i.toString();
              }
              let targetElement:HTMLElement | null | undefined = null;
              let selectionText = "";
              let start = 0;
              let end = 0;
              if(getSelection.rangeCount){
                if(getSelection.anchorNode?.nodeName === "#text"){
                  targetElement = getSelection.anchorNode.parentElement?.parentElement;
                }else{
                  targetElement = getSelection.anchorNode as HTMLElement;
                }
                selectionText = getSelection.toString();
                start = getSelection.getRangeAt(0).startOffset;
                end = getSelection.getRangeAt(0).endOffset;;
                setGlobalSelect({
                  selectionText: selectionText,
                  targetElement: targetElement,
                  start: start || 0,
                  end: end || 0
                })
              }
            }
          }}>
          <MDXEditor ref={ref} markdown={""}
              plugins={[headingsPlugin(),
                  toolbarPlugin({
              toolbarContents: () => ( <><UndoRedo /><BlockTypeSelect /><BoldItalicUnderlineToggles /></>)
          })]}
          />
        </div>
        </div>
  );
}

export default PDFPageEdit;