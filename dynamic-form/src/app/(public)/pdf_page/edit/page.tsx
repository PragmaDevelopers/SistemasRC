"use client";

import { useEffect, useRef, useState } from "react";
import { IFormSignUpAInputs } from "@/Interface/IFormInputs";
import { useRouter } from "next/navigation";
const Mustache = require('mustache');

import '@mdxeditor/editor/style.css'
import { MDXEditor,headingsPlugin,MDXEditorMethods,BlockTypeSelect,UndoRedo,BoldItalicUnderlineToggles,toolbarPlugin, corePluginHooks, InsertTable, tablePlugin } from "@mdxeditor/editor";
import { AlignCenterButton,AlignRightButton } from "@/components/mdxEditor/AlignButtons";
import { root } from "postcss";

function PDFPageEdit() {


  const [signUpData,setSignUpData] = useState<IFormSignUpAInputs>();
  const [variable,setVariable] = useState<string>("");

  const ref = useRef<MDXEditorMethods>(null)
  
  useEffect(()=>{
    const sessionData = sessionStorage.getItem("registration_form");
    if(sessionData){
      setSignUpData(JSON.parse(sessionData))
    }
    manipulateProseClass({restoreIds:true});
  },[])

  function manipulateProseClass({restoreIds}:{restoreIds?:boolean}){
    if(restoreIds){
      const editorWrapper = document.querySelectorAll(".prose p");
      for(let i = 0;i < editorWrapper.length;i++){
        editorWrapper[i].id = "line-"+i;
      }
    }
  }

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
    const lines = ref.current?.getMarkdown().split(/\n\n/g);
    const lineIndex = Number(sessionStorage.getItem("edit_pdf_line_selected")?.split("-")[1]);
    const wordIndex = Number(sessionStorage.getItem("edit_pdf_word_selected")?.split("-")[1]);
    const lineChildrenNumber = Number(sessionStorage.getItem(`edit_pdf_line_children_count`));
    console.log(lineIndex)
    if(lines){
      const line = lines[lineIndex];
      let replacementLine = line;
      console.log(line)

      if(!line?.match(/^<u>[\s]*<\/u>$/) && !line?.match(/^[ ]*$/)  && ![undefined,"&#x20;","****&#x20;","******&#x20;"]?.includes(line)){
        if(lineChildrenNumber > 1 || line.split("\n")[0].match(/^\| ([\s\S]*?) \|$/g)){
          
          const regexBoldUnderlineItalic = /(?:\*\*\*([\s\S]*?)\*\*\*|\*\*([\s\S]*?)\*\*|\*([\s\S]*?)\*|<u>([\s\S]*?)<\/u>|([^*]+)|<u>([^<]+)<\/u>)/g;
          const wordArr = line?.match(regexBoldUnderlineItalic);
          console.log(wordArr)
          if(wordArr){
            const replacementWord = specialCharVerification(wordArr,wordIndex);
            wordArr.splice(wordIndex,1,replacementWord);
            replacementLine = wordArr.join("");
          }

        }else{
          replacementLine = specialCharVerification(lines,lineIndex);
        }
      }else{
        replacementLine = variable;
      }
      lines.splice(lineIndex,1,replacementLine);
      const formattedLines = spaceVerification(lines);
      console.log(formattedLines)
      ref.current?.setMarkdown(formattedLines.join("\n\n"));
    }
  }

  function specialCharVerification(arr:RegExpMatchArray | string[],arrIndex:number){
    let specialCharNumber = 0; 
    if(arr[arrIndex].includes("*")){
      const boldNumber = arr[arrIndex]?.match(/\*/g)?.length;
      if(boldNumber){
        specialCharNumber += boldNumber / 2;
      }
    }
    if(arr[arrIndex].includes("<u>")){
      const underLineNumber = arr[arrIndex]?.match(/<u>|<\/u>/g)?.length;
      if(underLineNumber){
        specialCharNumber += underLineNumber + 1; 
      }
    }
    const startIndex = Number(sessionStorage.getItem("edit_pdf_start_index"));
    const modify = arr[arrIndex].replace(/[\\]/g,"").replace(/&#x20;/g," ").split("").map((char,index,arr)=>{
      if(startIndex - specialCharNumber === arr.length && index === arr.length - 1){
        char = char + variable;
      }else if(index === startIndex + specialCharNumber){
        char = variable + char;
      }
      return char;
    }).join("");
    return modify;
  }

  function spaceVerification(arr:string[]){
    const newArr:string[] = [];
        arr.forEach((line)=>{
          if(line.match(/^[ ]*$/)){
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
    console.log(textToPdf.split("\n\n"))
    const filterTextToPdf = spaceVerification(textToPdf.split("\n\n"));
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
        <div onSelect={()=>{
            const selection = window.getSelection();
              if(selection?.rangeCount){
                manipulateProseClass({restoreIds:true});
                let selectionText = selection.toString();
                let start = selection.getRangeAt(0).startOffset;
                sessionStorage.setItem("edit_pdf_start_index",start.toString())

                let targetElement:HTMLElement | null | undefined = null;
                let targetElementChild:HTMLElement | null | undefined = null;
                if(selection.anchorNode?.nodeName === "#text"){
                  targetElement = selection.anchorNode.parentElement?.parentElement;
                  targetElementChild = selection.anchorNode.parentElement
                }else{
                  targetElement = selection.anchorNode as HTMLElement;
                }
                
                if(targetElement){
                  sessionStorage.setItem(`edit_pdf_line_selected`,targetElement.id);
                  console.log(targetElement.id)
                  sessionStorage.setItem(`edit_pdf_line_children_count`,targetElement.children.length.toString());
                }
                const targetElementChildren = targetElement?.children;
                if(targetElementChildren){
                  for(let i = 0;i < targetElementChildren.length;i++){
                    targetElementChildren[i].id = "word-"+i;
                  }
                  if(targetElementChild){
                    sessionStorage.setItem(`edit_pdf_word_selected`,targetElementChild.id);
                  }
                }

              }
          }}>
          <MDXEditor contentEditableClassName="prose" ref={ref} markdown={""}
              plugins={[tablePlugin(),
                headingsPlugin(),
                  toolbarPlugin({
              toolbarContents: () => ( <><UndoRedo /><BlockTypeSelect /><BoldItalicUnderlineToggles /></>)
          })]}
          />
        </div>
        </div>
  );
}

export default PDFPageEdit;