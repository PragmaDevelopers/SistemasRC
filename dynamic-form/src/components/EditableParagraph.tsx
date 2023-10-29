import { useState,useEffect } from 'react';

export default function EditableParagraph({text,className}:{text:string,className:string}) {
  const [value, setValue] = useState<string>("");
  const [editablevalue, setEditableValue] = useState<string>("");
  const [config, setConfig] = useState<{left:number,top:number,width:number,height:number}>({left:0,top:0,width:0,height:0});
  const [modal, setModal] = useState<boolean>(false);

  useEffect(()=>{
    setValue(text)
  },[])

  function edit(e:any){
    setConfig({
        left: e.clientLeft,
        top: e.clientTop,
        height: e.clientHeight,
        width: e.clientWidth < 50 ? config.width + 50 : e.clientWidth
    })
    setEditableValue(value);
    setModal(true);
  }

  function submit(e:any){
    if(e.key === "Enter"){
        e.preventDefault();
        setValue(editablevalue);
        setModal(false);
    }
  }

  return (
    <div className={`relative inline-block ${className}`}>
        {modal && <input style={{left:config.left,top:config.top,width: config.width,height:config.height}} 
            className='bg-pink-200 absolute' type="text" onKeyDown={(e)=>submit(e)} 
            value={editablevalue} onChange={(e)=>setEditableValue(e.target.value)} 
        />}
        <p style={{minWidth: 20}} onClick={(e)=>{edit(e.target)}}>{value === "" ? "_____" : value }</p>
    </div>
  );
}