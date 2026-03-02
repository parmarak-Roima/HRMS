import React, { useEffect,useState } from 'react'
import { useParams } from 'react-router-dom'
import { getOrgChartByEmailId, getOrgChartByEmpId } from '../../Services/OrgChartService';
import OrgChartNode from '../../Componenets/OrgChart/OrgChartNode';
import { Search } from 'lucide-react';
import { handleGlobalError } from '../../Services/GlobalExceptionService';
import { useQuery, useQueryClient } from '@tanstack/react-query';


function OrgChart() {
  // const [loading, setLoading] = useState(true);
  const [chartData ,setChartData] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const {empId} = useParams();
  const [currentId, setCurrentId] = useState(empId)
  const queryClient = useQueryClient()

  const {data , isPending,error,isError} = useQuery(
    {
      queryKey:['chartData',currentId],
      queryFn: () =>  getOrgChartByEmpId(currentId),
      staleTime:5*60*200
    }
  );
  
    useEffect(() => {
      setChartData(data)
    }, [data])
   
  const handleSearch = async (e) => {
    try{
    e.preventDefault();
    const res =  await getOrgChartByEmailId(searchTerm);
    setChartData(res)
    setSearchTerm("");
    }catch(e){
      console.log(e)
      handleGlobalError(e);
    }
  };

  if(isError && error){
    console.log(error)
    handleGlobalError(error)
  }
  if (isPending) return <div className="text-center mt-20">Loading Org Chart...</div>;

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="flex justify-between mb-10">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Organization Chart</h1>
        </div>
        <form onSubmit={handleSearch} className=" bg-white border rounded px-3 py-2">
          <input 
            type="text" 
            placeholder="Search employee..." 
            className="outline-none text-sm w-64"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className='bg-black text-white rounded-2xl px-3 py-1' type="submit">Search</button>
        </form>
      </div>
      <div className="flex flex-col items-center">
        {chartData?.data?.pathFromRoot.map((emp) => (
          <div key={emp.id}>
            <OrgChartNode 
              employee={emp} 
              type="ancestor" 
              onClick={setCurrentId} 
            />
            <div className="w-px h-8  bg-gray-300 mx-auto"></div>
          </div>
        ))}
        <div>
          <OrgChartNode 
            employee={chartData?.data?.selectedEmployee} 
            type="focus" 
            onClick={() => {}} 
          />
          {chartData?.data?.directReports.length > 0 && <div className="w-px h-8  bg-gray-300 mx-auto"></div>}
        </div>
        {chartData?.data?.directReports.length > 0 && (
          <div>
             <div className="text-center">Direct Reports</div>
             <div className="flex gap-10 justify-center">
               {chartData?.data?.directReports.map((report) => (
                 <div key={report.id}>
                   <div className="w-px h-8  bg-gray-300 mx-auto"></div>
                    <OrgChartNode 
                      employee={report} 
                      type="report" 
                      onClick={setCurrentId} 
                    />
                 </div>
               ))}
             </div>
          </div>
        )}

      </div>
    </div>
  );
}

export default OrgChart