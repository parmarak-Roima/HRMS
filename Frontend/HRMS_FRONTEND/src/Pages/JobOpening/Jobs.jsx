import React, { useEffect, useState } from "react";
import { getAllActiveJobs, updateJobStatus } from "../../Services/jobService";
import { Loader } from "../../components/ui/Loader";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

function Jobs() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const { authUser, setAuthUser } = useAuthUserContext();
  const navigate = useNavigate();
  useEffect(() => {
    fetchActiveJobs();
  }, []);

  const fetchActiveJobs = async () => {
    try {
      const res = await getAllActiveJobs();
      console.log(res.data);
      setJobs(res.data);
      setLoading(false);
    } catch (e) {
      setLoading(false);
      handleGlobalError(e);
    } finally {
      setLoading(false);
    }
  };

  const closeJobOpening = async (jobId) => {
    try{
    await updateJobStatus(jobId,"CLOSED");
    toast.success("Job Closed SuccessFully!!")
    }catch(e){
      handleGlobalError(e);
    }
  }

  if (!loading && jobs.length == 0) {
    return <p>Job Openings not found !!</p>;
  }

  return (
    <>
      {loading ? (
        <Loader size={32} />
      ) : (
        <div className="max-w-4xl mx-auto space-y-6 w-full bg-gray-100 p-6">
          <div className="bg-white rounded-2xl shadow p-6">
            <div className="grid grid-cols-1">
              <h2 className="text-2xl text-center font-semibold mb-4 flex justify-around">
                Job-Openings
              </h2>
              <div className="flex justify-center mb-3">
                {authUser.role == "HR" && (
                  <button
                    onClick={() => {
                      navigate(`Create`);
                    }}
                    className="w-auto bg-black text-white font-medium py-2  px-3 rounded-2xl "
                  >
                    Create Job-Opening
                  </button>
                )}
              </div>
              <div className="space-y-4">
                {jobs.length === 0 ? (
                  <div className="text-center py-10 text-gray-500">
                    {" "}
                    "No active jobs yet!!!"{" "}
                  </div>
                ) : (
                  jobs.map((job) => (
                    <div
                      key={job.id}
                      className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                    >
                      <div className="flex-1">
                        <p className="text-sm text-gray-500">Title</p>
                        <p className="font-medium text-gray-800">{job.title}</p>
                        <p className="text-sm text-gray-500">Hr-Name</p>
                        <p className="font-medium text-gray-800">
                          {job.hrOwnerName}
                        </p>
                        <p className="text-sm text-gray-500">Status</p>
                        <p className="font-medium text-gray-800">
                          {job.status}
                        </p>
                        <p className="text-sm text-gray-500">Summary</p>
                        <p className="font-medium text-gray-800">
                          {job.summary}
                        </p>
                        <p className="text-sm text-gray-500">Description</p>
                        <p className="font-medium text-gray-800">
                          {job.description}
                        </p>
                        <div className="flex flex-row gap-2.5">
                          <a
                            href={job.jdFileUrl}
                            target="_blank"
                            className="w-auto px-3 mt-4 py-1 bg-black text-white text-sm rounded hover:bg-gray-700"
                          >
                            View Job-Description
                          </a>

                          <button
                              onClick={() =>
                                  navigate(`share/${job.id}`)
                              }
                            className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                          >
                            Share
                          </button>
                          <button
                              onClick={() =>
                                navigate(`referr/${job.id}`)
                              }
                            className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                          >
                            Reffer
                          </button>
                          {authUser.role == "HR" &&
                           <button
                              onClick={() =>
                                  closeJobOpening(job.id)
                              }
                            className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                          >
                            Close Job-Opening
                          </button>
                          }
                          <button
                              onClick={() =>
                                navigate(`/job-referrals/${job.id}`)  
                              }
                            className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                          >
                            All-referral
                          </button>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default Jobs;
