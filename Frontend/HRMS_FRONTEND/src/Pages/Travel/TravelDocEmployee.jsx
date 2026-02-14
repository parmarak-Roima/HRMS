import { useState, useEffect, use } from "react";
import { fetchAllDocs } from "../../Services/TravelDocService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useNavigate, useParams } from "react-router-dom";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
function TravelDocEmployee() {
  const {travelId,empId} = useParams();
  const [documents, setDocuments] = useState([]);
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();
  
  useEffect(() => {
    const getAllDocs = async () => {
      try {
        const response = await fetchAllDocs(travelId, empId);
        console.log(response);
        setDocuments(response.data);
      } catch (e) {
        handleGlobalError(e)
      }
    };
    getAllDocs();
  }, [travelId, empId]);
  
  if( documents.length == 0 ) return  <p  className="text-2xl text-center font-semibold mb-4">No document uploaded yet !!</p>
  return (
    <>
      <div className="w-full bg-gray-100 p-6">
        <div className="max-w-4xl mx-auto space-y-6">
          <div className="bg-white rounded-2xl shadow p-6">
            <div className="grid grid-cols-1">
              <h2 className="text-2xl text-center font-semibold mb-4">
                Travel Documents
              </h2>
                <div className="flex justify-end mb-3">
                    {(authUser.role == "EMPLOYEE" || authUser.role == "HR") && 
                  <button
                    onClick={() => {navigate(`/travel/uploadDocs/${empId}/${travelId}`)}}
                    className="w-25 bg-black text-white font-medium py-2  px-3 rounded-2xl "
                  >
                    upload document
                  </button>
                }   
                </div>
            </div>
            <div className="space-y-4">
              {documents?.map((doc) => (
                <div
                  key={doc.id}
                  className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                >
                  <div className="flex-1">
                    <p className="text-sm text-gray-500">Document Type</p>
                    <p className="font-medium text-gray-800">{doc.docType}</p>

                    <p className="text-sm text-gray-500 mt-2">Uploaded By</p>
                    <p className="font-medium text-gray-800">
                      {doc.uploadedByName}
                    </p>
                    <p className="text-sm text-gray-500 mt-2">Owner Type</p>
                    <p className="font-medium text-gray-800">
                      {doc.ownerId ? "EMPLOYEE" : "HR"}
                    </p>
                    <p className="text-sm text-gray-500 mt-2">Uploaded At</p>
                    <p className="font-medium text-gray-800">
                      {new Date(doc.uploadedAt).toLocaleString()}
                    </p>
                  </div>
                  <div className="flex flex-col justify-center">
                    <a
                      href={doc.fileUrl}
                      target="_blank"
                      className="px-3 py-1 bg-black text-white text-sm rounded hover:bg-blue-700 transition"
                    >
                      View
                    </a>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default TravelDocEmployee;
