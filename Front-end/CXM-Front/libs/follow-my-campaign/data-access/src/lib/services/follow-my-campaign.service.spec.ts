import {Params} from '@cxm-smartflow/shared/data-access/model';
import {
  emailCampaign,
  mockAddEmailCampaignSuccess,
  mockAddEmailCampaignFail,
  mockUpdateEmailCampaignFail,
  mockUpdateEmailCampaignSuccess,
  mockGetAllEmailCampaignSuccess,
  mockGetAllEmailCampaignFail,
  mockDeleteEmailCampaignFail,
  mockDeleteEmailCampaignSuccess,
  mockGetEmailCampaignByIdFail,
  mockGetEmailCampaignByIdSuccess,
  mockUploadFileSuccess,
  mockUploadFileFail
} from './../models/follow-my-campaign-test.model';
import { TestBed } from '@angular/core/testing';
import { FollowMyCampaignService } from './follow-my-campaign.service';
import { campaignEnv as env } from '@env-cxm-campaign';

import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import {
  mockGetAllCampaignsSuccess,
  mockGetListCampaignFail,
  mockGetListCampaignSuccess,
} from '../models/follow-my-campaign-test.model';

describe('FollowMyCampaignService', () => {
  let service: FollowMyCampaignService;
  let httpMock: HttpTestingController;
  let expectedResponse: any;
  const expectedUrl = env.apiURL;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [FollowMyCampaignService],
    });
    service = TestBed.inject(FollowMyCampaignService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // describe('Get all campaign', () => {
  //   describe('Test: get all campaigns success', () => {
  //     beforeEach(() => {
  //       service.getAllCampaign().subscribe((response) => {
  //         expectedResponse = response;
  //       });
  //       const req = httpMock.expectOne(
  //         expectedUrl + env.campaignContext + `/campaigns`
  //       );
  //       expect(req.request.method).toBe('GET');
  //       req.flush(mockGetAllCampaignsSuccess);
  //     });

  //     test('should return {status: 200, statusText: OK, response: body}', () => {
  //       expect(expectedResponse?.status).toEqual(200);
  //       expect(expectedResponse?.statusText).toEqual('OK');
  //       expect(expectedResponse?.response).not.toBeNull();
  //     });
  //   });
  // });

  describe('Upload file', () => {
    describe('Upload file success', () => {
      beforeEach(() => {
        const file = new File([""], "test.csv");
        service.uploadFile(file).subscribe((response) => {
          expectedResponse = response;
        });

        const req = httpMock.expectOne(
          expectedUrl + env.campaignContext + `/storage/store?dirs=tmp`
        );
        expect(req.request.method).toBe('POST');
        req.flush(mockUploadFileSuccess);
      });

      test('should return {status: 200, statusText: OK, response: body}', () => {
        expect(expectedResponse?.status).toEqual(200);
        expect(expectedResponse?.statusText).toEqual('OK');
        expect(expectedResponse?.response).not.toBeNull();
      });
    });

    describe('Upload file fail', () => {
      beforeEach(() => {
        const file = new File([""], "test.csv");
        service.uploadFile(file).subscribe((response) => {
          expectedResponse = response;
        });

        const req = httpMock.expectOne(
          expectedUrl + env.campaignContext + `/storage/store?dirs=tmp`
        );
        expect(req.request.method).toBe('POST');
        req.flush(mockUploadFileFail);
      });

      test('should return {status: 400, statusText: Bad Request, response: null}', () => {
        expect(expectedResponse?.body?.status).toEqual(400);
        expect(expectedResponse?.body?.statusText).toEqual('Bad Request');
        expect(expectedResponse?.body?.response).toBeNull();
      });
    });
  });

  // describe('Get list campaign', () => {
  //   describe('Test: get list campaign success', () => {
  //     beforeEach(() => {
  //       service.getList(1, 10).subscribe((response) => {
  //         expectedResponse = response;
  //       });
  //       const req = httpMock.expectOne(
  //         expectedUrl + '/campaign/api/v1/campaigns/sendMail/1/10'
  //       );
  //       expect(req.request.method).toBe('GET');
  //       req.flush(mockGetListCampaignSuccess);
  //     });

  //     test('should return {status: 200, statusText: OK, response: body}', () => {
  //       expect(expectedResponse?.status).toEqual(200);
  //       expect(expectedResponse?.statusText).toEqual('OK');
  //       expect(expectedResponse?.response).not.toBeNull();
  //     });
  //   });

  //   describe('Test: get list campaign fail', () => {
  //     beforeEach(() => {
  //       service.getList(0, 10).subscribe((response) => {
  //         expectedResponse = response;
  //       });
  //       const req = httpMock.expectOne(
  //         expectedUrl + '/campaign/api/v1/campaigns/sendMail/0/10'
  //       );
  //       expect(req.request.method).toBe('GET');
  //       req.flush(mockGetListCampaignFail);
  //     });

  //     test('should return {status: 400, statusText: Bad Request, response: null}', () => {
  //       expect(expectedResponse?.status).toEqual(400);
  //       expect(expectedResponse?.statusText).toEqual('Bad Request');
  //       expect(expectedResponse?.response).toBeNull();
  //     });
  //   });
  // });

  describe('Check validate csv file', () => {
    describe('Test: check validate csv files success', () => {
      beforeEach(() => {
        const headerArrays = ['test@gmail.com', 'nom', 'email'];
        service.checkValidateCSVFile(headerArrays, 1).subscribe((response) => {
          expectedResponse = response;
        });
        const req = httpMock.expectOne(
          expectedUrl +
            '/campaign/api/v1/campaigns/validate/csv/1?columns=test@gmail.com,nom,email'
        );
        req.flush(true);
      });

      test('should return true', () => {
        expect(expectedResponse).toBeTruthy();
      });
    });

    describe('Check validate csv files fail', () => {
      beforeEach(() => {
        const headerArrays = ['testing.com', 'nom', 'email'];
        service.checkValidateCSVFile(headerArrays, 1).subscribe((response) => {
          expectedResponse = response;
        });
        const req = httpMock.expectOne(
          expectedUrl +
            '/campaign/api/v1/campaigns/validate/csv/1?columns=testing.com,nom,email'
        );
        req.flush(false);
      });

      test('should return fail', () => {
        expect(expectedResponse).toBeFalsy();
      });
    });
  });

  describe('Email campaign', () => {
    describe('Add email campaign success', () => {
      beforeEach(() => {
        service.addEmailCampaign(emailCampaign).subscribe((response) => {
          expectedResponse = response;
        });

        const req = httpMock.expectOne(
          expectedUrl + '/campaign/api/v1/campaigns/email'
        );
        expect(req.request.method).toBe('POST');
        req.flush(mockAddEmailCampaignSuccess);
      });

      test('should return {status: 200, statusText: OK, response: body}', () => {
        expect(expectedResponse?.status).toEqual(200);
        expect(expectedResponse?.statusText).toEqual('OK');
        expect(expectedResponse?.response).not.toBeNull();
      });
    });

    describe('Add email campaign Fail', () => {
      beforeEach(() => {
        emailCampaign.templateId = 0;
        service.addEmailCampaign(emailCampaign).subscribe((response) => {
          expectedResponse = response;
        });

        const req = httpMock.expectOne(
          expectedUrl + '/campaign/api/v1/campaigns/email'
        );
        expect(req.request.method).toBe('POST');
        req.flush(mockAddEmailCampaignFail);
      });

      test('should return {status: 400, statusText: Bad Request, response: null}', () => {
        expect(expectedResponse?.status).toEqual(400);
        expect(expectedResponse?.statusText).toEqual('Bad Request');
        expect(expectedResponse?.response).toBeNull();
      });
    });

    // describe('Update email campaign success', () => {
    //   emailCampaign.id = 1;
    //   beforeEach(() => {
    //     service.updateEmailCampaign(emailCampaign).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email'
    //     );
    //     expect(req.request.method).toBe('PUT');
    //     req.flush(mockUpdateEmailCampaignSuccess);
    //   });

    //   test('should return {status: 200, statusText: OK, response: body}', () => {
    //     expect(expectedResponse?.status).toEqual(200);
    //     expect(expectedResponse?.statusText).toEqual('OK');
    //     expect(expectedResponse?.response).not.toBeNull();
    //   });
    // });

    // describe('Update email campaign fail', () => {
    //   emailCampaign.id = 0;
    //   beforeEach(() => {
    //     service.updateEmailCampaign(emailCampaign).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email'
    //     );
    //     expect(req.request.method).toBe('PUT');
    //     req.flush(mockUpdateEmailCampaignFail);
    //   });

    //   test('should return {status: 404, statusText: Not Found, response: null}', () => {
    //     expect(expectedResponse?.status).toEqual(404);
    //     expect(expectedResponse?.statusText).toEqual('Not Found');
    //     expect(expectedResponse?.response).toBeNull();
    //   });
    // });

    // describe('Get all email campaign success', () => {
    //   const params: Params = {
    //     page: 1,
    //     pageSize: 10
    //   };

    //   beforeEach(() => {
    //     service.getAllEmailCampaign(params).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/1/10'
    //     );

    //     expect(req.request.method).toBe('GET');
    //     req.flush(mockGetAllEmailCampaignSuccess);
    //   });

    //   test('should return {status: 200, statusText: OK, response: body}', () => {
    //     expect(expectedResponse?.status).toEqual(200);
    //     expect(expectedResponse?.statusText).toEqual('OK');
    //     expect(expectedResponse?.response).not.toBeNull();
    //   });
    // });

    // describe('Get all email campaign fail', () => {
    //   const params: Params = {
    //     page: 0,
    //     pageSize: 0
    //   };

    //   beforeEach(() => {
    //     service.getAllEmailCampaign(params).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/0/0'
    //     );

    //     expect(req.request.method).toBe('GET');
    //     req.flush(mockGetAllEmailCampaignFail);
    //   });

    //   test('should return {status: 400, statusText: Bad Request, response: null}', () => {
    //     expect(expectedResponse?.status).toEqual(400);
    //     expect(expectedResponse?.statusText).toEqual('Bad Request');
    //     expect(expectedResponse?.response).toBeNull();
    //   });
    // });


    // describe('Delete email campaign success', () => {
    //   const id = 1;
    //   beforeEach(() => {
    //     service.deleteEmailCampaign(id).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/1'
    //     );

    //     expect(req.request.method).toBe('PATCH');
    //     req.flush(mockDeleteEmailCampaignSuccess);
    //   });

    //   test('should return {status: 200, statusText: OK}', () => {
    //     expect(expectedResponse?.status).toEqual(200);
    //     expect(expectedResponse?.statusText).toEqual('OK');
    //   });
    // });

    // describe('Delete email campaign fail', () => {
    //   const id = 0;
    //   beforeEach(() => {
    //     service.deleteEmailCampaign(id).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/0'
    //     );

    //     expect(req.request.method).toBe('PATCH');
    //     req.flush(mockDeleteEmailCampaignFail);
    //   });

    //   test('should return {status: 404, statusText: Not Found}', () => {
    //     expect(expectedResponse?.status).toEqual(404);
    //     expect(expectedResponse?.statusText).toEqual('Not Found');
    //   });
    // });


    // describe('Get email campaign by id success', () => {
    //   const id = 1;
    //   beforeEach(() => {
    //     service.getEmailCampaignById(id).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/1'
    //     );

    //     expect(req.request.method).toBe('GET');
    //     req.flush(mockGetEmailCampaignByIdSuccess);
    //   });

    //   test('should return {status: 200, statusText: OK, response: body}', () => {
    //     expect(expectedResponse?.status).toEqual(200);
    //     expect(expectedResponse?.statusText).toEqual('OK');
    //     expect(expectedResponse?.response).not.toBeNull();
    //   });
    // });

    // describe('Get email campaign by id fail', () => {
    //   const id = 0;
    //   beforeEach(() => {
    //     service.getEmailCampaignById(id).subscribe((response) => {
    //       expectedResponse = response;
    //     });

    //     const req = httpMock.expectOne(
    //       expectedUrl + '/campaign/api/v1/campaigns/email/0'
    //     );

    //     expect(req.request.method).toBe('GET');
    //     req.flush(mockGetEmailCampaignByIdFail);
    //   });

    //   test('should return {status: 404, statusText: Not Found, response: null}', () => {
    //     expect(expectedResponse?.status).toEqual(404);
    //     expect(expectedResponse?.statusText).toEqual('Not Found');
    //     expect(expectedResponse?.response).toBeNull();
    //   });
    // });
  });
});
