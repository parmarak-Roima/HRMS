import React, { useEffect, useState } from "react";
import {
  getReferralByJobId,
  updateJobReferralStatus,
} from "../../Services/JobReferralService";
import { useNavigate, useParams } from "react-router-dom";
import { handleGlobalError } from "../../Services/GlobalExceptionService";
import { Loader } from "../../components/ui/Loader";
import { useAuthUserContext } from "../../Contexts/AuthUserContext";
import { toast } from "react-toastify";
const statuses = ["NEW", "IN_REVIEW", "INTERVIEWING", "REJECTED", "HIRED"];
function JobReferrals() {
  const { jobId } = useParams();
  const [jobReferrals, setjobReferrals] = useState([]);
  const [loading, setLoading] = useState(true);
  const { authUser, setAuthUser } = useAuthUserContext();
  const [jobReferralId, setJobReferralId] = useState();
  const [status, setStatus] = useState();
  useEffect(() => {
    fetchJobReferrals();
  }, []);

  const fetchJobReferrals = async () => {
    try {
      const res = await getReferralByJobId(jobId);
      console.log(res.data);
      setjobReferrals(res.data);
      setLoading(false);
    } catch (e) {
      handleGlobalError(e);
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  const submitStatusChange = async () => {
    try {
      await updateJobReferralStatus(jobReferralId, status);
      fetchJobReferrals();
      setJobReferralId(null);
      toast.success("status updated successFully!!");
    } catch (e) {
      handleGlobalError(e);
    }
  };

  return (
    <>
      {jobReferralId ? (
        <div className="max-w-lg mx-auto p-6 bg-white rounded-lg shadow mt-10">
          <div className="flex content-center justify-center mt-5 items-center gap-2">
            <label className="block text-sm font-medium">
              Set Referral-Status
            </label>
            <select
              className="p-2 px-5 border-2 rounded-full"
              onChange={(e) => {
                console.log(e.target.value);
                setStatus(e.target.value);
              }}
            >
              <option>Choose</option>
              {statuses.map((status) => (
                <option value={status}>{status}</option>
              ))}
            </select>
            <button
              className="px-4 py-2 bg-black text-white rounded hover:bg-gray-700"
              onClick={() => {
                if (!status) {
                  toast.warn("choose status");
                  return;
                }
                submitStatusChange();
              }}
            >
              Submit
            </button>
          </div>
        </div>
      ) : (
        <>
          {loading ? (
            <Loader size={32} />
          ) : (
            <div className="max-w-4xl mx-auto space-y-6 w-full bg-gray-100 p-6">
              <div className="bg-white rounded-2xl shadow p-6">
                <div className="grid grid-cols-1">
                  <h2 className="text-2xl text-center font-semibold mb-4 flex justify-around">
                    Job-referrals
                  </h2>
                  <div className="space-y-4">
                    {jobReferrals.length === 0 ? (
                      <div className="text-center py-10 text-gray-500">
                        No - Job referrals found for you !!!
                      </div>
                    ) : (
                      jobReferrals.map((jobReferral) => (
                        <div
                          key={jobReferral.id}
                          className="bg-white shadow rounded-lg p-4 border border-gray-200 flex items-start gap-4"
                        >
                          <div className="flex-1">
                            <p className="text-sm text-gray-500">Title</p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.jobTitle}
                            </p>
                            <p className="text-sm text-gray-500">
                              Referrer-Email
                            </p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.referrerEmail}
                            </p>
                            <p className="text-sm text-gray-500">
                              Candidate-Email
                            </p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.candidateEmail}
                            </p>
                            <p className="text-sm text-gray-500">
                              Candidate-Name
                            </p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.candidateName}
                            </p>
                            <p className="text-sm text-gray-500">Note</p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.note}
                            </p>
                            <p className="text-sm text-gray-500">Status</p>
                            <p className="font-medium text-gray-800">
                              {jobReferral.status}
                            </p>
                          </div>
                          <div className="flex flex-row gap-2.5">
                            <a
                              href={jobReferral.resumeUrl}
                              target="_blank"
                              className="w-auto px-3 mt-4 py-1 bg-black text-white text-sm rounded hover:bg-gray-700"
                            >
                              Resume
                            </a>
                            {authUser.role == "HR" && (
                              <button
                                onClick={() => {
                                  setJobReferralId(jobReferral.id);
                                }}
                                className="w-auto bg-black text-white text-sm py-1 px-3 mt-4 rounded  hover:bg-gray-700 "
                              >
                                Change Status
                              </button>
                            )}
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
      )}
    </>
  );
}

export default JobReferrals;
